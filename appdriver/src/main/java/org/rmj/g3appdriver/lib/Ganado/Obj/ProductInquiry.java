package org.rmj.g3appdriver.lib.Ganado.Obj;

import static org.rmj.g3appdriver.etc.AppConstants.getLocalMessage;

import android.app.Application;

import androidx.lifecycle.LiveData;

import org.rmj.g3appdriver.GCircle.room.DataAccessObject.DGanadoOnline;
import org.rmj.g3appdriver.GCircle.room.Entities.EMCColor;
import org.rmj.g3appdriver.GCircle.room.Entities.EMcBrand;
import org.rmj.g3appdriver.GCircle.room.Entities.EMcModel;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;
import org.rmj.g3appdriver.lib.Ganado.pojo.InstallmentInfo;
import org.rmj.gocas.pricelist.PriceFactory;
import org.rmj.gocas.pricelist.Pricelist;

import java.util.List;

public class ProductInquiry {
    private static final String TAG = ProductInquiry.class.getSimpleName();

    private final DGanadoOnline poDao;
    private final Pricelist poPrice;

    private String message;

    public ProductInquiry(Application instance) {
        this.poDao = GGC_GCircleDB.getInstance(instance).ganadoDao();
        this.poPrice = PriceFactory.make(PriceFactory.ProductType.MOTORCYCLE);
    }

    public String getMessage() {
        return message;
    }

    public static String[] getPaymentForm(){
        return new String[]{"Cash", "Installment"};
    }

    public LiveData<List<EMcBrand>> GetMotorcycleBrands(){
        return poDao.getAllMcBrand();
    }

    public LiveData<List<EMcModel>> GetModelsList(String BrandID){
        return poDao.getAllModeFromBrand(BrandID);
    }
    public LiveData<EMcModel> GetModel(String BrandID, String ModelID){
        return poDao.getModeFromBrand(BrandID,ModelID);
    }

    public LiveData<List<EMCColor>> GetModelColor(String ModelID){
        return poDao.GetModelColors(ModelID);
    }
    public DGanadoOnline.McInfo GetMCInfo(String ModelID, String BrandID, String ColorID){
        return poDao.GetMCInfo(ModelID, BrandID, ColorID);
    }

    public DGanadoOnline.McAmortization GetMonthlyPayment(String ModelID, int Term){
        return poDao.GetMonthlyPayment(ModelID, Term);
    }

    public DGanadoOnline.McDownpayment GetInstallmentPlanDetail(String ModelID){
        return poDao.getDownpayment(ModelID);
    }

    public LiveData<DGanadoOnline.CashPrice> GetCashPrice(String ModelID){
        return poDao.GetCashInfo(ModelID);
    }

    /**
     *
     * @param ModelID pass the selected model ID.
     * @return validate if the return value is 0 process means
     *          the process has failed and needed to call getMessage()
     *
     *          NOTE: the default installment term set is 36 months
     */
    public InstallmentInfo GetMinimumDownpayment(String ModelID){
        try{
            DGanadoOnline.McDownpayment loDownPy = poDao.getDownpayment(ModelID);

            if(loDownPy == null){
                message = "Sorry, we encountered an issue while retrieving the minimum down payment.";
                return null;
            }

            org.json.simple.JSONObject joDownPy = new org.json.simple.JSONObject();
            joDownPy.put("sModelIDx", loDownPy.ModelIDx);
            joDownPy.put("sModelNme", loDownPy.ModelNme);
            joDownPy.put("nRebatesx", loDownPy.Rebatesx);
            joDownPy.put("nMiscChrg", loDownPy.MiscChrg);
            joDownPy.put("nEndMrtgg", loDownPy.EndMrtgg);
            joDownPy.put("nMinDownx", loDownPy.MinDownx);
            joDownPy.put("nSelPrice", loDownPy.SelPrice);
            joDownPy.put("nLastPrce", loDownPy.LastPrce);

            poPrice.setModelInfo(joDownPy);

            double lnMinDown = poPrice.getMinimumDP();

            DGanadoOnline.McAmortization loAmort = poDao.GetMonthlyPayment(ModelID, 36);

            if(loAmort == null){
                message = "Apologies, we are unable to calculate the monthly amortization at the moment.";
                return null;
            }

            org.json.simple.JSONObject joAmort = new org.json.simple.JSONObject();
            joAmort.put("nSelPrice", loAmort.nSelPrice);
            joAmort.put("nMinDownx", loAmort.nMinDownx);
            joAmort.put("nMiscChrg", loAmort.nMiscChrg);
            joAmort.put("nRebatesx", loAmort.nRebatesx);
            joAmort.put("nEndMrtgg", loAmort.nEndMrtgg);
            joAmort.put("nAcctThru", loAmort.nAcctThru);
            joAmort.put("nFactorRt", loAmort.nFactorRt);

            poPrice.setPaymentTerm(36);

            double lnAmort = poPrice.getMonthlyAmort(joAmort);

            InstallmentInfo loInfo = new InstallmentInfo(
                    lnMinDown,
                    lnAmort);
            return loInfo;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return null;
        }
    }

    /**
     *
     * @param ModelID pass the selected model ID
     * @param Term selected installment term
     *             48 Months
     *             36 Months
     *             24 Months
     *             12 Months
     *             6 Months
     * @return the monthly amortization, if the value is equal to 0
     *          means the process failed and needs to call getMessage()
     *          to retrieve error message.
     */
    public double GetMonthlyAmortization(String ModelID, int Term){
        try{
            DGanadoOnline.McAmortization loAmort = poDao.GetMonthlyPayment(ModelID, Term);

            if(loAmort == null){
                message = "Apologies, we are unable to calculate the monthly amortization at the moment.";
                return 0;
            }

            org.json.simple.JSONObject joAmort = new org.json.simple.JSONObject();
            joAmort.put("nSelPrice", loAmort.nSelPrice);
            joAmort.put("nMinDownx", loAmort.nMinDownx);
            joAmort.put("nMiscChrg", loAmort.nMiscChrg);
            joAmort.put("nRebatesx", loAmort.nRebatesx);
            joAmort.put("nEndMrtgg", loAmort.nEndMrtgg);
            joAmort.put("nAcctThru", loAmort.nAcctThru);
            joAmort.put("nFactorRt", loAmort.nFactorRt);

            poPrice.setPaymentTerm(Term);

            double lnAmort = poPrice.getMonthlyAmort(joAmort);

            return lnAmort;
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return 0;
        }
    }

    public double GetMonthlyAmortization(String ModelID, int Term, double args1){
        try{
            DGanadoOnline.McAmortization loAmort = poDao.GetMonthlyPayment(ModelID, Term);

            if(loAmort == null){
                message = "Apologies, we are unable to calculate the monthly amortization at the moment.";
                return 0;
            }

            org.json.simple.JSONObject joAmort = new org.json.simple.JSONObject();
            joAmort.put("nSelPrice", loAmort.nSelPrice);
            joAmort.put("nMinDownx", loAmort.nMinDownx);
            joAmort.put("nMiscChrg", loAmort.nMiscChrg);
            joAmort.put("nRebatesx", loAmort.nRebatesx);
            joAmort.put("nEndMrtgg", loAmort.nEndMrtgg);
            joAmort.put("nAcctThru", loAmort.nAcctThru);
            joAmort.put("nFactorRt", loAmort.nFactorRt);

            poPrice.setDownPayment(args1);
            return poPrice.getMonthlyAmort(joAmort);
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return 0;
        }
    }
    public double GetMonthlyAmortization(DGanadoOnline.McAmortization args, double args1){
        try{
            org.json.simple.JSONObject loJson = new org.json.simple.JSONObject();
            loJson.put("nSelPrice", ((args.nSelPrice == null) ? 0:args.nSelPrice));
            loJson.put("nMinDownx", args.nMinDownx);
            loJson.put("nMiscChrg", args.nMiscChrg);
            loJson.put("nRebatesx", args.nRebatesx);
            loJson.put("nEndMrtgg", args.nEndMrtgg);
            loJson.put("nAcctThru", args.nAcctThru);
            loJson.put("nFactorRt", args.nFactorRt);

            poPrice.setDownPayment(args1);
            return poPrice.getMonthlyAmort(loJson);
        } catch (Exception e){
            e.printStackTrace();
            message = getLocalMessage(e);
            return 0;
        }
    }
}
