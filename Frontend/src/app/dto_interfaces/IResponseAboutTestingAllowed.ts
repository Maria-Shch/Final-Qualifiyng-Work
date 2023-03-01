import {IReasonOfProhibitionTesting} from "./IReasonOfProhibitionTesting";

export interface IResponseAboutTestingAllowed{
  testingAllowed : boolean;
  reasonOfProhibition : IReasonOfProhibitionTesting | null;
}
