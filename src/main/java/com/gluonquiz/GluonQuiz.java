package com.gluonquiz;

import com.gluonhq.charm.glisten.application.MobileApplication;
import com.gluonquiz.views.LoginView;
import javafx.scene.Scene;

public class GluonQuiz extends MobileApplication {

    public static final String PRIMARY_VIEW = HOME_VIEW;
    
    @Override
    public void init() {
        addViewFactory(PRIMARY_VIEW, () -> new LoginView(PRIMARY_VIEW).getView());
    }

    @Override
    public void postInit(Scene scene) {
    }
}