package com.paymentManagement.view;

import com.paymentManagement.service.impl.AgentServiceImpl;
import com.paymentManagement.service.interfaces.AgentService;


public class MainMenu {
    private final AgentServiceImpl agentService;
    private final InputHandler inputHandler;

    public MainMenu()
    {
        this.agentService = new AgentServiceImpl();
        this.inputHandler = new InputHandler();
    }
}
