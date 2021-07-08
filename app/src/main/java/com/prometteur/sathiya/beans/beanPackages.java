package com.prometteur.sathiya.beans;

import java.io.Serializable;

/**
 * Created by Ravi on 10/24/2016.
 */
public class beanPackages implements Serializable {
    String planId,planCallLimit,planCallRate;

    public beanPackages(String planId, String planCallLimit,String planCallRate) {
        this.planId = planId;
        this.planCallLimit = planCallLimit;
        this.planCallRate = planCallRate;

    }

    public String getPlanId() {
        return planId;
    }

    public void setPlanId(String planId) {
        this.planId = planId;
    }

    public String getPlanCallLimit() {
        return planCallLimit;
    }

    public void setPlanCallLimit(String planCallLimit) {
        this.planCallLimit = planCallLimit;
    }

    public String getPlanCallRate() {
        return planCallRate;
    }

    public void setPlanCallRate(String planCallRate) {
        this.planCallRate = planCallRate;
    }
}
