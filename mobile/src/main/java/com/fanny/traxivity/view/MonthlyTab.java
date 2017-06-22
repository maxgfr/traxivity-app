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
import com.fanny.traxivity.history.StepsManager;

import java.util.ArrayList;
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

        StepsManager managerSteps = StepsManager.getInstance();


        Map<Integer, Integer> mapStepsDayByHour = managerSteps.getStepPerDayOneMonth();
        myListActivity = new ArrayList<>(mapStepsDayByHour.size());
        for(Map.Entry<Integer, Integer> entry : mapStepsDayByHour.entrySet()){
            myListActivity.add(entry.getKey()+" DAYS AGO"+" - "+entry.getValue()+" STEPS");
        }

        Collections.sort(myListActivity, new Comparator<String>() {
            public int compare(String o1, String o2)
            {
                char num1 = o1.charAt(0);
                char num2 = o1.charAt(1);
                char num3 = o2.charAt(0);
                char num4 = o2.charAt(1);
                String sub1 =  new StringBuilder().append(num1).toString();
                String sub2 =  new StringBuilder().append(num3).toString();
                return sub1.compareTo (sub2);
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_list_item_1, myListActivity);
        listView.setAdapter(adapter);
        return v;
    }
}
