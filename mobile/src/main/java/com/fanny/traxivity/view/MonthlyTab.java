package com.fanny.traxivity.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.fanny.traxivity.R;
import com.fanny.traxivity.database.activity.ActivityManager;
import com.fanny.traxivity.database.activity.DbActivity;
import com.fanny.traxivity.database.stepsManagerBeta.DbSteps;
import com.fanny.traxivity.database.stepsManagerBeta.StepsManager;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;


/**
 * Created by huextrat <www.hugoextrat.com>.
 */

public class MonthlyTab extends Fragment {
    private List<String> myListActivity;
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v =inflater.inflate(R.layout.monthly_tab,container,false);

        ListView listView = (ListView) v.findViewById(R.id.listview);

        StepsManager managerSteps = new StepsManager();


        Map<Integer, Integer> mapStepsDayByHour = managerSteps.getTotalStepsDayByHours(new Date());
        myListActivity = new ArrayList<>(mapStepsDayByHour.size());
        for(Map.Entry<Integer, Integer> entry : mapStepsDayByHour.entrySet()){
            myListActivity.add(entry.getKey()+"h"+" - "+entry.getValue()+" steps");
        }

        Collections.sort(myListActivity, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return  o1.compareTo(o2);
            }
        });

        Collections.reverse(myListActivity);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, myListActivity);
        listView.setAdapter(adapter);
        return v;
    }
}
