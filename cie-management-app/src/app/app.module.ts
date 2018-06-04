import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppComponent} from './app.component';
import {UserComponent} from './user/user-component/user.component';
import {UserService} from './user/user.service';
import {AppRoutingModule} from './app.routing.module';
import {HttpClientModule} from '@angular/common/http';
import {FormsModule, ReactiveFormsModule} from '@angular/forms';
import {JwtModule} from '@auth0/angular-jwt';
import {LoginComponent} from './login/login.component';
import {DashboardComponent} from './dashboard/dashboard.component';
import {ProtectedDirective} from './directives/protected.directive';
import {CustomMaterialModule} from './material.module';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {CreateUserComponent} from './user/create/create-user.component';
import {EditUserComponent} from './user/edit/edit-user.component';
import {UserTableComponent} from './user/table/user-table.component';

export function tokenGetter() {
  return localStorage.getItem('access_token');
}

@NgModule({
  declarations: [
    AppComponent,
    UserComponent,
    CreateUserComponent,
    EditUserComponent,
    UserTableComponent,
    LoginComponent,
    DashboardComponent,
    ProtectedDirective
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,
    BrowserAnimationsModule,
    CustomMaterialModule,
    JwtModule.forRoot({
      config: {
        tokenGetter: tokenGetter,
        whitelistedDomains: ['localhost:8080', 'localhost:4200'],
        blacklistedRoutes: []
      }
    })
  ],
  providers: [UserService],
  bootstrap: [AppComponent]
})
export class AppModule {
}
