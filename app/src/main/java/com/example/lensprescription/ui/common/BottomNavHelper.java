package com.example.lensprescription.ui.common;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lensprescription.R;
import com.example.lensprescription.ui.addedit.AddPrescriptionActivity;
import com.example.lensprescription.ui.insights.InsightsActivity;
import com.example.lensprescription.ui.list.ViewPrescriptionsActivity;
import com.example.lensprescription.ui.main.MainActivity;

public class BottomNavHelper {

    public static final int NAV_HOME     = 0;
    public static final int NAV_ADD      = 1;
    public static final int NAV_RECORDS  = 2;
    public static final int NAV_INSIGHTS = 3;

    public static void setup(Activity activity, int activeTab) {

        View root = activity.findViewById(R.id.bottomNav);
        if (root == null) return;

        View navHome     = root.findViewById(R.id.navHome);
        View navAdd      = root.findViewById(R.id.navAdd);
        View navRecords  = root.findViewById(R.id.navRecords);
        View navInsights = root.findViewById(R.id.navInsights);

        // Highlight each tab
        highlight(root, R.id.navHomeLabel,     R.id.navHomeIcon,     activeTab == NAV_HOME,     activity);
        highlight(root, R.id.navAddLabel,      R.id.navAddIcon,      activeTab == NAV_ADD,      activity);
        highlight(root, R.id.navRecordsLabel,  R.id.navRecordsIcon,  activeTab == NAV_RECORDS,  activity);
        highlight(root, R.id.navInsightsLabel, R.id.navInsightsIcon, activeTab == NAV_INSIGHTS, activity);

        // Click listeners
        navHome.setOnClickListener(v -> {
            if (activeTab != NAV_HOME) {
                Intent i = new Intent(activity, MainActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(i);
                activity.finish();
            }
        });

        navAdd.setOnClickListener(v ->
                activity.startActivity(new Intent(activity, AddPrescriptionActivity.class)));

        navRecords.setOnClickListener(v -> {
            if (activeTab != NAV_RECORDS) {
                Intent i = new Intent(activity, ViewPrescriptionsActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(i);
                activity.finish();
            }
        });

        navInsights.setOnClickListener(v -> {
            if (activeTab != NAV_INSIGHTS) {
                Intent i = new Intent(activity, InsightsActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                activity.startActivity(i);
                activity.finish();
            }
        });
    }

    private static void highlight(View root, int labelId, int iconId,
                                  boolean active, Activity activity) {

        TextView  label = root.findViewById(labelId);
        ImageView icon  = root.findViewById(iconId);

        int activeColor   = activity.getResources().getColor(R.color.colorPrimary,    activity.getTheme());
        int inactiveColor = activity.getResources().getColor(R.color.text_secondary,  activity.getTheme());

        if (label != null) {
            label.setTextColor(active ? activeColor : inactiveColor);
            label.setTypeface(null, active
                    ? android.graphics.Typeface.BOLD
                    : android.graphics.Typeface.NORMAL);
        }

        if (icon != null) {
            // Tint active → colorPrimary, inactive → text_secondary (grey)
            icon.setImageTintList(ColorStateList.valueOf(active ? activeColor : inactiveColor));
        }
    }
}