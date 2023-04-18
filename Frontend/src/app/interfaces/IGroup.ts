import {IUser} from "./IUser";
import {ILevelOfEdu} from "./ILevelOfEdu";
import {IProfile} from "./IProfile";
import {IFaculty} from "./IFaculty";
import {IFormOfEdu} from "./IFormOfEdu";
import {IYear} from "./IYear";

export interface IGroup {
  id: number | null;
  levelOfEdu: ILevelOfEdu | null;
  profile: IProfile | null;
  faculty: IFaculty | null;
  formOfEdu: IFormOfEdu | null;
  courseNumber: number | null;
  groupNumber: number | null;
  year: IYear | null;
  name: string | null;
  teacher: IUser | null;
}
