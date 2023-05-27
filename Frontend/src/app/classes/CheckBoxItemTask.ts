export class CheckBoxItemTask {
  value: number;
  label: string;
  checked: boolean;
  taskSerialNumber: number;

  constructor(taskSerialNumber: number, value: any, label: any, checked?: boolean) {
    this.taskSerialNumber = taskSerialNumber;
    this.value = value;
    this.label = label;
    this.checked = checked ? checked : false;
  }
}
