import { Directive, ElementRef, HostListener } from '@angular/core';

/**
 * This directive removes focus from the selectors after clicking on them.
 */
@Directive({
  selector: 'button.remove-focus, input.remove-focus'
})
export class FocusRemoverDirective {
  constructor(private elRef: ElementRef) {
  }

  @HostListener('click') onClick() {
    this.elRef.nativeElement.blur();
  }
}
