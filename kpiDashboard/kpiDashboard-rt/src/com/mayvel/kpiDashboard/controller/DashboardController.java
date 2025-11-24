package com.mayvel.kpiDashboard.controller;

import com.mayvel.kpiDashboard.BRestApiServerService;
import com.tridium.json.JSONArray;
import com.tridium.json.JSONObject;
import javax.baja.control.BBooleanWritable;
import javax.baja.control.BNumericWritable;
import javax.baja.naming.BOrd;
import javax.baja.status.BStatusBoolean;
import javax.baja.status.BStatusNumeric;
import javax.baja.sys.BAbsTime;
import javax.baja.sys.BComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DashboardController  {

    // ================================================================
    // GET /getData
    // ================================================================
    public JSONObject getAllPoints(BComponent parent, String isSavedParam) {
        JSONObject response = new JSONObject();
        JSONArray pointsArray = new JSONArray();

        // 1️⃣ Collect all points (no filter)
        collectPoints(parent, pointsArray);

        // 2️⃣ Apply filter if isSavedParam is provided
        if (isSavedParam != null) {
            boolean filterSaved = "true".equalsIgnoreCase(isSavedParam);
            boolean filterUnsaved = "false".equalsIgnoreCase(isSavedParam);

            JSONArray filteredArray = new JSONArray();
            for (int i = 0; i < pointsArray.length(); i++) {
                JSONObject point = pointsArray.getJSONObject(i);
                boolean isSaved = point.optBoolean("isSaved", false);

                if ((filterSaved && isSaved) || (filterUnsaved && !isSaved)) {
                    filteredArray.put(point);
                }
            }
            pointsArray = filteredArray; // replace original array with filtered one
        }

        // 3️⃣ Build response
        response.put("status", "success");
        response.put("points", pointsArray);
        response.put("timestamp", BAbsTime.now().toString());

        return response;
    }

    private void collectPoints(BComponent parent, JSONArray pointsArray) {

        String[] savedPoints = getAllSavedPoints();   // list of saved point names

        for (BComponent comp : parent.getChildComponents()) {

            // ****************************************
            // Helper: check if this point is saved
            // ****************************************
            String pointName = comp.getName();
            boolean isSaved = Arrays.asList(savedPoints).contains(pointName);

            // ============ NUMERIC WRITABLE ============
            if (comp instanceof BNumericWritable) {
                BNumericWritable num = (BNumericWritable) comp;
                JSONObject o = new JSONObject();
                o.put("pointName", num.getName());
                o.put("type", "NumericWritable");
                o.put("isSaved", isSaved);     // <-- ADD THIS


                Double val = num.getOutStatusValue().isNull()
                        ? null
                        : num.getOut().getValue();

                o.put("value", val == null ? null : Math.round(val));

                JSONObject pri = new JSONObject();
                for (int i = 1; i <= 16; i++) {
                    BStatusNumeric pVal = getNumericPriorityValue(num, i);
                    if (pVal == null || pVal.isNull() || !pVal.getStatus().isOk())
                        pri.put("In" + i, JSONObject.NULL);
                    else
                        pri.put("In" + i, Math.round(pVal.getValue()));
                }
                o.put("priorityArray", pri);
                pointsArray.put(o);
            }

            // ============ BOOLEAN WRITABLE ============
            else if (comp instanceof BBooleanWritable) {
                BBooleanWritable bool = (BBooleanWritable) comp;
                JSONObject o = new JSONObject();
                o.put("pointName", bool.getName());
                o.put("type", "BooleanWritable");
                o.put("isSaved", isSaved);      // <-- ADD THIS

                Boolean val = bool.getOutStatusValue().isNull()
                        ? null
                        : bool.getOut().getValue();

                o.put("value", val);

                JSONObject pri = new JSONObject();
                for (int i = 1; i <= 16; i++) {
                    BStatusBoolean pVal = getBooleanPriorityValue(bool, i);
                    if (pVal == null || pVal.isNull() || !pVal.getStatus().isOk())
                        pri.put("In" + i, JSONObject.NULL);
                    else
                        pri.put("In" + i, pVal.getValue());
                }
                o.put("priorityArray", pri);
                pointsArray.put(o);
            }

            // RECURSE
            if (comp.getChildComponents().length > 0)
                collectPoints(comp, pointsArray);
        }
    }

    private String[] getAllSavedPoints() {
        String savedPoints = "";   // FIXED (valid Java)

        try {
            // Get all HS Network paths
            String[] paths = getAllHSNetworkPaths();

            if (paths.length == 0) {
                return new String[0]; // nothing found
            }

            // Resolve HTTP server service
            BOrd ord = BOrd.make(paths[0]);
            BComponent comp = (BComponent) ord.resolve().get();

            if (comp instanceof BRestApiServerService) {
                savedPoints = ((BRestApiServerService) comp).getSavedPoints();
            }

        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        // 🔥 If no points saved → return empty array
        if (savedPoints == null || savedPoints.trim().isEmpty()) {
            return new String[0];
        }

        // 🔥 Split CSV into array
        return savedPoints.split(",");
    }

    // ================================================================
    // Fetch individual priority slot
    // ================================================================
    private BStatusNumeric getNumericPriorityValue(BNumericWritable num, int priority) {
        switch (priority) {
            case 1: return num.getIn1();
            case 2: return num.getIn2();
            case 3: return num.getIn3();
            case 4: return num.getIn4();
            case 5: return num.getIn5();
            case 6: return num.getIn6();
            case 7: return num.getIn7();
            case 8: return num.getIn8();
            case 9: return num.getIn9();
            case 10: return num.getIn10();
            case 11: return num.getIn11();
            case 12: return num.getIn12();
            case 13: return num.getIn13();
            case 14: return num.getIn14();
            case 15: return num.getIn15();
            case 16: return num.getIn16();
        }
        return null;
    }

    private BStatusBoolean getBooleanPriorityValue(BBooleanWritable bool, int priority) {
        switch (priority) {
            case 1: return bool.getIn1();
            case 2: return bool.getIn2();
            case 3: return bool.getIn3();
            case 4: return bool.getIn4();
            case 5: return bool.getIn5();
            case 6: return bool.getIn6();
            case 7: return bool.getIn7();
            case 8: return bool.getIn8();
            case 9: return bool.getIn9();
            case 10: return bool.getIn10();
            case 11: return bool.getIn11();
            case 12: return bool.getIn12();
            case 13: return bool.getIn13();
            case 14: return bool.getIn14();
            case 15: return bool.getIn15();
            case 16: return bool.getIn16();
        }
        return null;
    }

    public static String[] getAllHSNetworkPaths() {
        try {
            // Start from the root of the station
            BOrd ord = BOrd.make("station:|slot:/");
            BComponent root = (BComponent) ord.resolve().get();

            // List to hold all matching paths
            List<String> paths = new ArrayList<>();

            // Recursively collect all paths
            collectHSNPaths(root, paths);

            return paths.toArray(new String[0]);
        } catch (Exception e) {
            e.printStackTrace();
            return new String[0];
        }
    }

    private static void collectHSNPaths(BComponent parent, List<String> paths) {
        try {
            if (parent instanceof BRestApiServerService) {
                paths.add("station:|"+parent.getSlotPath().toString());
            }

            BComponent[] children = parent.getChildComponents();
            for (BComponent child : children) {
                collectHSNPaths(child, paths);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
