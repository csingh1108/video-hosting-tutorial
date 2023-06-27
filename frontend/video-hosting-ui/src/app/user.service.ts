import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";

@Injectable({
  providedIn: 'root'
})
export class UserService {

  private userId: string = '';

  constructor(private httpClient: HttpClient) { }

  subscribeToUser(userId: string){
    return this.httpClient.post<boolean>("http://localhost:8080/api/user/subscribe/"+userId, null)
  }

  registerUser() {
    this.httpClient.get("http://localhost:8080/api/user/register", {responseType:"text"})
      .subscribe(data => {
        this.userId= data;
      })
  }

  getUserId(){
    return this.userId;
  }

  unSubscribeToUser(userId: string) {
    return this.httpClient.post<boolean>("http://localhost:8080/api/user/unsubscribe/"+userId, null)
  }
}
