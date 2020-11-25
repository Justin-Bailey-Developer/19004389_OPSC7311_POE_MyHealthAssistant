package com.healthpack.myhealthassistant;

public class Singleton {

    // static variable single_instance of type Singleton
    private static Singleton single_instance = null;

    public DataManager dataManager;
    public ConversionManager conversionManager;

    // private constructor restricted to this class itself
    private Singleton()
    {
        dataManager = new DataManager();
        conversionManager = new ConversionManager();
    }

    // static method to create instance of Singleton class
    public static Singleton getInstance()
    {
        if (single_instance == null) {
            single_instance = new Singleton();
        }
        return single_instance;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public void setDataManager(DataManager dataManager) {
        this.dataManager = dataManager;
    }

    public ConversionManager getConversionManager() {
        return conversionManager;
    }

    public void setConversionManager(ConversionManager conversionManager) {
        this.conversionManager = conversionManager;
    }
}
