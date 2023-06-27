import { NgModule } from '@angular/core';
import { AuthModule } from 'angular-auth-oidc-client';


@NgModule({
    imports: [AuthModule.forRoot({
        config: {
            authority: 'https://dev-hrca0v8hflbsiu1v.us.auth0.com',
            redirectUrl: 'http://localhost:4200/callback',
            postLogoutRedirectUri: 'http://localhost:4200/featured',
            clientId: 'ZbJpf9GjJRi8AWcqRodraJc3rlZf0yjp',
            scope: 'openid profile offline_access email',
            responseType: 'code',
            silentRenew: true,
            useRefreshToken: true,
            secureRoutes:['http://localhost:8080/'],
            customParamsAuthRequest: {
              audience: 'http://localhost:8080',
            }

        }
      })],
    exports: [AuthModule],
})
export class AuthConfigModule {}
