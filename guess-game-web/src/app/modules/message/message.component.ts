import { Component, Input, OnInit } from "@angular/core";
import { MessageService } from "./message.service";
import { Message } from "./message.model";

@Component({
  selector: "app-message",
  templateUrl: "message.component.html",
})
export class MessageComponent implements OnInit {
  private lastMessage: Message;
  @Input() private autoHide = false;
  @Input() private hidingTime = 10;

  constructor(private messageService: MessageService) {
  }

  ngOnInit(): void {
    this.messageService.messages.subscribe(m => this.lastMessage = m);
  }

  isVisible(): boolean {
    if (this.lastMessage) {
      if (this.autoHide) {
        if (this.lastMessage.date) {
          let timeDifference = Date.now() - this.lastMessage.date.getTime();

          return timeDifference <= this.hidingTime * 1000;
        } else {
          return true;
        }
      } else {
        return true;
      }
    } else {
      return false;
    }
  }
}
