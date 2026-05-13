package com.roadguardian.backend.model;

public class DashboardMetrics {

    private int totalAccidents;
    private int criticalCases;
    private int resolvedCases;

    public DashboardMetrics() {
    }

    public DashboardMetrics(
            int totalAccidents,
            int criticalCases,
            int resolvedCases
    ) {
        this.totalAccidents = totalAccidents;
        this.criticalCases = criticalCases;
        this.resolvedCases = resolvedCases;
    }

    public int getTotalAccidents() {
        return totalAccidents;
    }

    public int getCriticalCases() {
        return criticalCases;
    }

    public int getResolvedCases() {
        return resolvedCases;
    }

    public void setTotalAccidents(int totalAccidents) {
        this.totalAccidents = totalAccidents;
    }

    public void setCriticalCases(int criticalCases) {
        this.criticalCases = criticalCases;
    }

    public void setResolvedCases(int resolvedCases) {
        this.resolvedCases = resolvedCases;
    }
}