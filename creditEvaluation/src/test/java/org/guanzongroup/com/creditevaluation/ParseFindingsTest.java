package org.guanzongroup.com.creditevaluation;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.guanzongroup.com.creditevaluation.Core.FindingsParser;
import org.guanzongroup.com.creditevaluation.Core.oChildFndg;
import org.guanzongroup.com.creditevaluation.Core.oParentFndg;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.List;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ParseFindingsTest {
    private static final String TAG = ParseFindingsTest.class.getSimpleName();

    @Mock
    HashMap<oParentFndg, List<oChildFndg>> loDetail;

    @Mock
    FindingsParser loParser = new FindingsParser();

    @Test
    public void test01ParseForEvaluationDoubleValue() throws Exception{
        MockitoAnnotations.initMocks(FindingsParser.class);
        String lsVal = "{\"employed\":{\"sEmployer\":\"NULL\",\"sWrkAddrx\":\"NULL\",\"sPosition\":\"NULL\",\"nLenServc\":-1.0,\"nSalaryxx\":-1.0},\"self_employed\":{\"sBusiness\":\"NULL\",\"sBusAddrx\":\"NULL\",\"nBusLenxx\":0.0,\"nBusIncom\":0.0,\"nMonExpns\":0.0},\"financed\":{\"sFinancer\":\"NULL\",\"sReltnDsc\":\"NULL\",\"sCntryNme\":\"NULL\",\"nEstIncme\":0.0},\"pensioner\":{\"sPensionx\":\"NULL\",\"nPensionx\":0.0}}";
        String lsLab = "{\"employed\":{\"sEmployer\":\"United Phlp And Phper Inc.\",\"sWrkAddrx\":\"Km 48 Mc Arthur Hi-way Iba Este, Calumpit Bulacan\",\"sPosition\":\"M009680\",\"nLenServc\":23.0,\"nSalaryxx\":35000.0},\"self_employed\":null,\"financed\":null,\"pensioner\":null}";
        loDetail = loParser.getForEvaluation(oChildFndg.FIELDS.MEANS, lsVal, lsLab);
        assertNotNull(loDetail);
        assertTrue(loDetail.size() > 0);
    }
}
