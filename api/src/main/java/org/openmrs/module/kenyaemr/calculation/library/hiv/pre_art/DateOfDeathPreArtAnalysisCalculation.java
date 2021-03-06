/**
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/. OpenMRS is also distributed under
 * the terms of the Healthcare Disclaimer located at http://openmrs.org/license.
 *
 * Copyright (C) OpenMRS Inc. OpenMRS is a registered trademark and the OpenMRS
 * graphic logo is a trademark of OpenMRS Inc.
 */
package org.openmrs.module.kenyaemr.calculation.library.hiv.pre_art;

import org.openmrs.PatientProgram;
import org.openmrs.Program;
import org.openmrs.calculation.patient.PatientCalculationContext;
import org.openmrs.calculation.result.CalculationResultMap;
import org.openmrs.calculation.result.SimpleResult;
import org.openmrs.module.kenyacore.calculation.AbstractPatientCalculation;
import org.openmrs.module.kenyacore.calculation.Calculations;
import org.openmrs.module.kenyaemr.calculation.EmrCalculationUtils;
import org.openmrs.module.kenyaemr.calculation.library.hiv.art.InitialArtStartDateCalculation;
import org.openmrs.module.kenyaemr.calculation.library.rdqa.DateOfDeathCalculation;
import org.openmrs.module.kenyaemr.metadata.HivMetadata;
import org.openmrs.module.metadatadeploy.MetadataUtils;
import org.openmrs.module.reporting.common.DateUtil;
import org.openmrs.module.reporting.common.DurationUnit;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Created by codehub on 9/8/15.
 */
public class DateOfDeathPreArtAnalysisCalculation extends AbstractPatientCalculation {

    @Override
    public CalculationResultMap evaluate(Collection<Integer> cohort, Map<String, Object> params, PatientCalculationContext context) {

        Integer outcomePeriod = (params != null && params.containsKey("outcomePeriod")) ? (Integer) params.get("outcomePeriod") : null;
        Program hivProgram = MetadataUtils.existing(Program.class, HivMetadata._Program.HIV);
        CalculationResultMap enrollment = Calculations.firstEnrollments(hivProgram, cohort, context);

        if(outcomePeriod != null){
            context.setNow(DateUtil.adjustDate(DateUtil.getStartOfMonth(context.getNow()), outcomePeriod, DurationUnit.MONTHS));
        }

        CalculationResultMap deadPatientsMap = calculate(new DateOfDeathCalculation(), cohort, context);
        CalculationResultMap artStartDate = calculate(new InitialArtStartDateCalculation(), cohort, context);

        CalculationResultMap ret = new CalculationResultMap();

        for(Integer ptId:cohort){
            Date result = EmrCalculationUtils.datetimeResultForPatient(deadPatientsMap, ptId);
            Date artDate = EmrCalculationUtils.datetimeResultForPatient(artStartDate, ptId);
            PatientProgram patientProgram = EmrCalculationUtils.resultForPatient(enrollment, ptId);
            Date deathDate = null;
            if(patientProgram != null && outcomePeriod != null && result != null) {
                Date futureDate = DateUtil.adjustDate(DateUtil.adjustDate(patientProgram.getDateEnrolled(), outcomePeriod, DurationUnit.MONTHS), 1, DurationUnit.DAYS);
                if (result.before(futureDate)) {
                    deathDate = result;

                }
                if(artDate != null && artDate.after(patientProgram.getDateEnrolled()) &&  artDate.before(futureDate)){
                    deathDate = null;
                }
            }
            ret.put(ptId, new SimpleResult(deathDate, this));
        }
        return ret;
    }
}
