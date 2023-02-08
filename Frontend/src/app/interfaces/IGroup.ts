import {IUser} from "./IUser";
import {ILevelOfEdu} from "./ILevelOfEdu";
import {IProfile} from "./IProfile";
import {IFaculty} from "./IFaculty";
import {IFormOfEdu} from "./IFormOfEdu";

export interface IGroup {
  id: number | null;
  levelOfEdu: ILevelOfEdu | null;
  profile: IProfile | null;
  faculty: IFaculty | null;
  formOfEdu: IFormOfEdu | null;
  courseNumber: number | null;
  groupNumber: number | null;
  year: number | null;
  name: string | null;
  teacher: IUser | null;
}
