package main.java.controller;

public class SlaveController {
    public MasterController masterController;

    public void initMasterController(MasterController masterController) {
        this.masterController = masterController;
    }
}
