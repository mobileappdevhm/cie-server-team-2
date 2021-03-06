package edu.hm.cs.cieserver.campus;

import edu.hm.cs.cieserver.notification.NotificationRequest;
import edu.hm.cs.cieserver.notification.NotificationService;
import edu.hm.cs.cieserver.util.Switches;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * Controller handling campus purposes.
 */
@RestController
@RequestMapping({"/api/locations"})
public class CampusController {

	@Autowired
	private CampusRepository campusRepository;

	@Autowired
	private NotificationService notificationService;

	@Value("${app.cie-server.maps-api-key}")
	private String mapsApiKey;

	@GetMapping(path = "/maps-api-key")
	public String getMapsApiKey() {
		return mapsApiKey;
	}

	@GetMapping(path = "/image/{id}")
	public byte[] getImage(@PathVariable("id") Long id) {
		return campusRepository.findById(id).map(Campus::getImage).orElse(new byte[0]);
	}

	@GetMapping(path = {"/{id}"})
	public Campus findOne(@PathVariable("id") Long id) {
		return campusRepository.findById(id).get();
	}

	@GetMapping
	public List<Campus> findAll() {
		return campusRepository.findAll();
	}

	@PostMapping
	public Campus create(@RequestBody CampusRequest campusRequest) {
		Campus campus = campusRequest.getCampus();

		if (campusRequest.getBase64Image() != null) {
			byte[] imageByte = Base64.decodeBase64(campusRequest.getBase64Image());

			campus.setImage(imageByte);
		}

		Campus c = campusRepository.save(campus);

		notifyOfCampusChanges();

		return c;
	}

	@PutMapping
	public Campus update(@RequestBody CampusRequest campusRequest) {
		Optional<Campus> existing = campusRepository.findById(campusRequest.getCampus().getId());

		if (existing.isPresent()) {
			Campus campus = campusRequest.getCampus();
			if (campusRequest.getBase64Image() == null) {
				campus.setImage(existing.get().getImage());
			} else {
				// Update image.
				byte[] imageByte = Base64.decodeBase64(campusRequest.getBase64Image());

				campus.setImage(imageByte);
			}

			Campus c = campusRepository.save(campus);

			notifyOfCampusChanges();

			return c;
		}

		return null;
	}

	@DeleteMapping(path = {"/{id}"})
	public void delete(@PathVariable("id") Long id) {
		campusRepository.deleteById(id);

		notifyOfCampusChanges();
	}

	/**
	 * Notify all users of the campus changes.
	 */
	private void notifyOfCampusChanges() {
		if (Switches.ENABLE_NOTIFICATIONS) {
			List<Campus> campuses = campusRepository.findAll();

			NotificationRequest request = new NotificationRequest("campus_changes", "changes", campuses, Optional.of("/topics/changes"));

			notificationService.send(request);
		}
	}

}
