import {Component, OnInit} from '@angular/core';
import {ActivatedRoute} from "@angular/router";
import {VideoService} from "../video.service";
import {UserService} from "../user.service";

@Component({
  selector: 'app-video-details',
  templateUrl: './video-details.component.html',
  styleUrls: ['./video-details.component.css']
})
export class VideoDetailsComponent implements OnInit{

  videoId='';
  videoUrl!: string;
  videoAvailable:boolean = false;
  videoTitle !: string;
  videoDescription !: string;
  tags: Array<String> = [];
  likeCount: number = 0;
  dislikeCount: number= 0;
  viewCount: number =0;
  showSubscribeButton:boolean = true;
  showUnsubscribeButton:boolean = false;



  constructor(private activatedRoute: ActivatedRoute,
              private videoService: VideoService,
              private userService: UserService) {
    this.videoId= this.activatedRoute.snapshot.params.videoId;
    this.videoService.getVideo(this.videoId).subscribe(data => {
      this.videoUrl= data.videoUrl;
      this.videoTitle = data.title;
      this.videoDescription = data.description;
      this.tags = data.tags;
      this.videoAvailable = true;
      this.likeCount = data.likeCount;
      this.dislikeCount = data.dislikeCount;
      this.viewCount = data.viewCount;
    })
  }
  ngOnInit(): void {
  }

  likeVideo() {
    this.videoService.likeVideo(this.videoId).subscribe(data => {
    this.likeCount=data.likeCount;
    this.dislikeCount=data.dislikeCount})
  }

  dislikeVideo() {
    this.videoService.dislikeVideo(this.videoId).subscribe(data => {
      this.likeCount=data.likeCount;
      this.dislikeCount=data.dislikeCount})
  }

  subscribeToUser() {
    let userId = this.userService.getUserId();
    this.userService.subscribeToUser(userId).subscribe(data => {
      this.showSubscribeButton = false;
      this.showUnsubscribeButton = true;
    })
  }

  unSubscribeToUser() {
    let userId = this.userService.getUserId();
    this.userService.unSubscribeToUser(userId).subscribe(data => {
      this.showSubscribeButton = true;
      this.showUnsubscribeButton = false;
    })
  }
}
