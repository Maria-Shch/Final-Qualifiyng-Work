import {AbstractControl, FormControl, FormGroup, ValidationErrors} from "@angular/forms";

export class FormUtils {
  /**
   * Get all errors of FormGroup
   * @param control - target form control
   */
  static getControlErrors(control: AbstractControl): ValidationErrors | null {
    if (control instanceof FormControl) {
      // Return FormControl errors or null
      return control.errors ?? null;
    }
    if (control instanceof FormGroup) {
      const groupErrors = control.errors;
      // Form group can contain errors itself, in that case add'em
      const formErrors = groupErrors ? { groupErrors } : {};
      Object.keys(control.controls).forEach(key => {
        // Recursive call of the FormG fields
        // @ts-ignore
        const error = FormUtils.getControlErrors(control.get(key));
        if (error !== null) {
          // Only add error if not null
          // @ts-ignore
          formErrors[key] = error;
        }
      });
      // Return FormGroup errors or null
      return Object.keys(formErrors).length > 0 ? formErrors : null;
    }
    return null;
  }
}
