package org.rmj.guanzongroup.evaluation.Callback;

import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDMaster;
import org.rmj.g3appdriver.GCircle.room.Entities.ESSDDepartments;

public interface OnSSDDItemClick {
    void OnSelectDepartment(ESSDDepartments loDepartment);
    void OnSelectEvaluation(ESSDDMaster loMaster);
}