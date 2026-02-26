package org.rmj.g3appdriver;

/**
 * DATE UPDATED : 2026-01-24
 * AUTHOR: GUILLIER GUTOMAN
 * **/

import android.database.Cursor;

import androidx.room.Room;
import androidx.room.migration.Migration;
import androidx.room.testing.MigrationTestHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;
import androidx.sqlite.db.framework.FrameworkSQLiteOpenHelperFactory;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.rmj.g3appdriver.GCircle.room.GGC_GCircleDB;

import java.io.IOException;

public class MigrationTest {

    private static Boolean CheckColumnExists(SupportSQLiteDatabase database, String tablename, String columnName){
        Cursor cursor = null;
        try {
            cursor = database.query("SELECT * FROM " + tablename + " LIMIT 0");
            int index = cursor.getColumnIndex(columnName);

            if (index < 0){
                cursor.close();
                return false;
            }else {
                cursor.close();
                return true;
            }

        }catch (Exception e){
            cursor.close();
            e.printStackTrace();
            return false;
        }
    }

    static final Migration MIGRATION_V44 = new Migration(43, 44) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

            // Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS `MC_Cash_Price` " +
                    "(`sModelIDx` TEXT NOT NULL, `sMCCatNme` TEXT NOT NULL, " +
                    "`sModelNme` TEXT NOT NULL, `sBrandNme` TEXT, `nSelPrice` REAL, " +
                    "`nLastPrce` REAL, `nDealrPrc` REAL, `dPricexxx` TEXT, " +
                    "`sBrandIDx` TEXT, `sMCCatIDx` TEXT, " +
                    "PRIMARY KEY(`sModelIDx`, `sMCCatNme`, `sModelNme`))");

            // Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS `SCA_Rqst_Emp` " +
                    "(`sSCACodex` TEXT NOT NULL, `sEmployIDx` TEXT NOT NULL, " +
                    "`sRecdStat` TEXT, `dTimeStmpx` TEXT, " +
                    "PRIMARY KEY(`sSCACodex`, `sEmployIDx`))");

            //Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS Barcode " +
                    "(barcode_id TEXT NOT NULL, barcode TEXT, " +
                    "PRIMARY KEY(barcode_id))");

            //Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS Barcode_Detail " +
                    "(barcode_id TEXT NOT NULL, nEntryNox INTEGER NOT NULL, " +
                    " sDescript TEXT, " +
                    "PRIMARY KEY(barcode_id, nEntryNox))");

            if (!CheckColumnExists(database, "Ganado_Online", "nCashPrce")){
                database.execSQL("ALTER TABLE Ganado_Online ADD COLUMN nCashPrce REAL");
            }
            if (!CheckColumnExists(database, "Ganado_Online", "dPricexxx")){
                database.execSQL("ALTER TABLE Ganado_Online ADD COLUMN dPricexxx TEXT");
            }
            if (!CheckColumnExists(database, "Credit_Applicant_Info", "sRemarksx")){
                database.execSQL("ALTER TABLE Credit_Applicant_Info ADD COLUMN sRemarksx TEXT");
            }
            if (!CheckColumnExists(database, "Barcode", "checked")){
                database.execSQL("ALTER TABLE Barcode ADD COLUMN checked INTEGER DEFAULT 0");
            }
            if (!CheckColumnExists(database, "Barcode", "description")){
                database.execSQL("ALTER TABLE Barcode ADD COLUMN description TEXT");
            }
        }
    };

    static final Migration MIGRATION_V45 = new Migration(44, 45) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

            // Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS `MC_Cash_Price` " +
                    "(`sModelIDx` TEXT NOT NULL, `sMCCatNme` TEXT NOT NULL, " +
                    "`sModelNme` TEXT NOT NULL, `sBrandNme` TEXT, `nSelPrice` REAL, " +
                    "`nLastPrce` REAL, `nDealrPrc` REAL, `dPricexxx` TEXT, " +
                    "`sBrandIDx` TEXT, `sMCCatIDx` TEXT, " +
                    "PRIMARY KEY(`sModelIDx`, `sMCCatNme`, `sModelNme`))");

            // Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS `SCA_Rqst_Emp` " +
                    "(`sSCACodex` TEXT NOT NULL, `sEmployIDx` TEXT NOT NULL, " +
                    "`sRecdStat` TEXT, `dTimeStmpx` TEXT, " +
                    "PRIMARY KEY(`sSCACodex`, `sEmployIDx`))");

            //Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS Barcode " +
                    "(barcode_id TEXT NOT NULL, barcode TEXT, " +
                    "PRIMARY KEY(barcode_id))");

            //Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS Barcode_Detail " +
                    "(barcode_id TEXT NOT NULL, nEntryNox INTEGER NOT NULL, " +
                    "sSerialID TEXT, sDescript TEXT, " +
                    "PRIMARY KEY(barcode_id, nEntryNox))");

            //Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS User_Guides " +
                    "(sTransNox TEXT NOT NULL, sTitlexx TEXT, sURlxx TEXT, " +
                    "PRIMARY KEY(sTransNox))");

            //Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS Policy_Menus " +
                    "(sTransNoxx TEXT NOT NULL, sNamexx TEXT, " +
                    "PRIMARY KEY(sTransNoxx))");

            //Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS Article_Head " +
                    "(sCodexx TEXT NOT NULL, sTitle TEXT, sImage TEXT, sDescription TEXT, sSubtitle TEXT, " +
                    "PRIMARY KEY(sCodexx))");

            //Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS Policy_Contents " +
                    "(sTransNoxx TEXT NOT NULL, sParentIDxx TEXT, sContentxx TEXT, " +
                    "PRIMARY KEY(sTransNoxx))");

            //Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS Article_Details " +
                    "(sCodexx TEXT NOT NULL, sContentxx TEXT, " +
                    "PRIMARY KEY(sCodexx))");

            if (!CheckColumnExists(database, "Ganado_Online", "nCashPrce")){
                database.execSQL("ALTER TABLE Ganado_Online ADD COLUMN nCashPrce REAL");
            }
            if (!CheckColumnExists(database, "Ganado_Online", "dPricexxx")){
                database.execSQL("ALTER TABLE Ganado_Online ADD COLUMN dPricexxx TEXT");
            }
            if (!CheckColumnExists(database, "Credit_Applicant_Info", "sRemarksx")){
                database.execSQL("ALTER TABLE Credit_Applicant_Info ADD COLUMN sRemarksx TEXT");
            }
            if (!CheckColumnExists(database, "Barcode", "checked")){
                database.execSQL("ALTER TABLE Barcode ADD COLUMN checked INTEGER DEFAULT 0");
            }
            if (!CheckColumnExists(database, "Barcode", "description")){
                database.execSQL("ALTER TABLE Barcode ADD COLUMN description TEXT");
            }
            if (!CheckColumnExists(database, "Barcode_Detail", "sSerialID")){
                database.execSQL("ALTER TABLE Barcode_Detail ADD COLUMN sSerialID TEXT");
            }
        }
    };

    /**
     * DATE UPDATED : 2026-01-24
     * VERSION : 46
     * **/
    static final Migration MIGRATION_V46 = new Migration(45, 46) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {

            // Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS `MC_Cash_Price` " +
                    "(`sModelIDx` TEXT NOT NULL, `sMCCatNme` TEXT NOT NULL, " +
                    "`sModelNme` TEXT NOT NULL, `sBrandNme` TEXT, `nSelPrice` REAL, " +
                    "`nLastPrce` REAL, `nDealrPrc` REAL, `dPricexxx` TEXT, " +
                    "`sBrandIDx` TEXT, `sMCCatIDx` TEXT, " +
                    "PRIMARY KEY(`sModelIDx`, `sMCCatNme`, `sModelNme`))");

            // Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS `SCA_Rqst_Emp` " +
                    "(`sSCACodex` TEXT NOT NULL, `sEmployIDx` TEXT NOT NULL, " +
                    "`sRecdStat` TEXT, `dTimeStmpx` TEXT, " +
                    "PRIMARY KEY(`sSCACodex`, `sEmployIDx`))");

            //Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS Barcode " +
                    "(barcode_id TEXT NOT NULL, barcode TEXT, " +
                    "PRIMARY KEY(barcode_id))");

            //Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS Barcode_Detail " +
                    "(barcode_id TEXT NOT NULL, nEntryNox INTEGER NOT NULL, " +
                    "sSerialID TEXT, sDescript TEXT, " +
                    "PRIMARY KEY(barcode_id, nEntryNox))");

            //Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS User_Guides " +
                    "(sTransNox TEXT NOT NULL, type TEXT NOT NULL, sTitlexx TEXT, sURlxx TEXT, " +
                    "PRIMARY KEY(sTransNox, type))");

            //Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS Policy_Menus " +
                    "(sTransNoxx TEXT NOT NULL, sNamexx TEXT, sDescription TEXT, " +
                    "PRIMARY KEY(sTransNoxx))");

            //Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS Article_Head " +
                    "(sCodexx TEXT NOT NULL, sTitle TEXT, sImage TEXT, sDescription TEXT, sSubtitle TEXT, " +
                    "PRIMARY KEY(sCodexx))");

            //Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS Policy_Contents " +
                    "(sTransNoxx TEXT NOT NULL, sParentIDxx TEXT, sContentxx TEXT, " +
                    "PRIMARY KEY(sTransNoxx))");

            //Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS Article_Details " +
                    "(sCodexx TEXT NOT NULL, sContentxx TEXT, " +
                    "PRIMARY KEY(sCodexx))");

            // Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS `Error_Logs` " +
                    "(`nErrorLogID` INTEGER NOT NULL, `sSourceTransNo` TEXT, " +
                    "`sMessagex` TEXT, `dLogDate` TEXT, `cRead` TEXT, "+
                    "PRIMARY KEY(`nErrorLogID`))");

            // Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS `CAS_Approval_Code` " +
                    "(`sSourceCD` TEXT NOT NULL, `sDescript` TEXT, " +
                    "PRIMARY KEY(`sSourceCD`))");

            // Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS `CAS_Requests` " +
                    "(`sTransNox` TEXT NOT NULL, `dTransact` TEXT, `sSourceCD` TEXT," +
                    "`sSourceNo` TEXT, `sAuthType` TEXT, `sDescript` TEXT, `sCompnyNm` TEXT, `sRemarksx` TEXT," +
                    "`cTranStat` TEXT, `dApproved` TEXT, `sAppSrcNo` TEXT," +
                    "PRIMARY KEY(`sTransNox`))");

            //Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS `SSDD_Department` " +
                    "(`sDeptIDxx` TEXT NOT NULL, `sDescript` TEXT, " +
                    "PRIMARY KEY(`sDeptIDxx`))");

            //Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS `SSDD_Categories` " +
                    "(`sCategrID` TEXT NOT NULL, `sDescript` TEXT, `sMemoLink` TEXT, `nPageNumber` TEXT, " +
                    "PRIMARY KEY(`sCategrID`))");

            //Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS `SSDD_Master` " +
                    "(`sTransNox` TEXT NOT NULL, `dTransact` TEXT, `cTranStat` TEXT, `sDeptIDxx` TEXT, " +
                    "PRIMARY KEY(`sTransNox`))");

            //Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS `SSDD_Detail` " +
                    "(sTransNox TEXT NOT NULL, sCategrID TEXT NOT NULL, nRatingxx REAL, sRemarksx TEXT, dEvaluate TEXT, PRIMARY KEY(sTransNox, sCategrID))");

            //Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS `SSDD_Images` (" +
                    "sTransNox TEXT NOT NULL, sReferNox TEXT, sCategrID TEXT, nEntryNox TEXT, " +
                    "sScanndID TEXT, sImageNme TEXT, sMD5Hashx TEXT, sImagePth TEXT, dImgeDate TEXT, cImgeStat TEXT, PRIMARY KEY(sTransNox))");

            //Add the new table
            database.execSQL("CREATE TABLE IF NOT EXISTS `MC_Contract_Info` (" +
                    "sTransNox TEXT NOT NULL, sBranchCd TEXT, dTransact TEXT, sClientID TEXT, " +
                    "sReferNox TEXT, sAcctNmbr TEXT, dPurchase TEXT, sSerialID TEXT, nAcctTerm INTEGER, nDownPaym DOUBLE, nMonAmort DOUBLE, " +
                    "nRebatesx DOUBLE, nPenaltyx DOUBLE, dFirstPay TEXT, sRemarksx TEXT, cTranStat TEXT, sSendStat TEXT, " +
                    "PRIMARY KEY(sTransNox))");

            if (!CheckColumnExists(database, "Ganado_Online", "nCashPrce")){
                database.execSQL("ALTER TABLE Ganado_Online ADD COLUMN nCashPrce REAL");
            }
            if (!CheckColumnExists(database, "Ganado_Online", "dPricexxx")){
                database.execSQL("ALTER TABLE Ganado_Online ADD COLUMN dPricexxx TEXT");
            }
            if (!CheckColumnExists(database, "Credit_Applicant_Info", "sRemarksx")){
                database.execSQL("ALTER TABLE Credit_Applicant_Info ADD COLUMN sRemarksx TEXT");
            }
            if (!CheckColumnExists(database, "Barcode", "checked")){
                database.execSQL("ALTER TABLE Barcode ADD COLUMN checked INTEGER DEFAULT 0");
            }
            if (!CheckColumnExists(database, "Barcode", "description")){
                database.execSQL("ALTER TABLE Barcode ADD COLUMN description TEXT");
            }
            if (!CheckColumnExists(database, "Barcode_Detail", "sSerialID")){
                database.execSQL("ALTER TABLE Barcode_Detail ADD COLUMN sSerialID TEXT");
            }
            if (!CheckColumnExists(database, "Image_Information", "sClientID")){
                database.execSQL("ALTER TABLE EImageInfo ADD COLUMN sClientID TEXT");
            }
            if (!CheckColumnExists(database, "LR_DCP_Collection_Detail", "sEWalletReference")){
                database.execSQL("ALTER TABLE LR_DCP_Collection_Detail ADD COLUMN sEWalletReference TEXT");
            }
        }
    };

    @Rule
    public MigrationTestHelper migrationTestHelper = new MigrationTestHelper(
            InstrumentationRegistry.getInstrumentation(),
            GGC_GCircleDB.class.getCanonicalName(),
            new FrameworkSQLiteOpenHelperFactory()
    );

    @Test
    public void TestMIgration() throws IOException {

        //create database with version
        SupportSQLiteDatabase db = migrationTestHelper.createDatabase("GGC_ISysDBF.db", 46);
        db.close();

        //migrate database to new version
        GGC_GCircleDB appDB = Room.databaseBuilder(
                InstrumentationRegistry.getInstrumentation().getTargetContext(),
                GGC_GCircleDB.class,
                "GGC_ISysDBF.db"
        ).addMigrations(MIGRATION_V45, MIGRATION_V46)
                .build();

        appDB.getOpenHelper().getWritableDatabase();
        appDB.close();

        Assert.assertTrue(migrationTestHelper.runMigrationsAndValidate("GGC_ISysDBF.db", 46, true, MIGRATION_V45, MIGRATION_V46).isDatabaseIntegrityOk());
    }
}
