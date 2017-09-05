package com.example.gxt.jlwlcokapp;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.codbking.widget.DatePickDialog;
import com.codbking.widget.bean.DateType;
import com.example.gxt.jlwlcokapp.web.WebServicePost;

//import com.example.web.WebServicePost;


/*
* *
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RecordFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RecordFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecordFragment extends Fragment {

    private Spinner building;
    private Spinner floor;
    private Spinner classroom;
    private TextView tv_feedback;
    private EditText item1;
    private EditText item2;


    private Button key;
    private String userid;
    private String mClassroom;
    // 返回主线程更新数据
    private static Handler handler = new Handler();
    private String s;


    private String startTime;
    private String endTime;
    String[] classbar = new String[]{"00", "00", "00"};
    String[] buildingdata = new String[]{" ", "博弈", "笃行"};
    String[] floordata = new String[]{" ", "三楼", "四楼", "五楼", "六楼"};
    String[] classroomdata = new String[]{" ", "02", "03", "04", "05", "06"};


        // 子线程接收数据，主线程修改数据
        public class MyThread implements Runnable {

            @Override
            public void run() {
               s = WebServicePost.executeHttpPost(userid,mClassroom,"ApplyServlet");
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        tv_feedback.setText(s);
                        Toast.makeText(getActivity(),s,Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_record, container, false);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        Bundle bundle = getArguments();
        userid = bundle.getString("DATA1");
        item1 = (EditText) getActivity().findViewById(R.id.item1);
        item1.setInputType(InputType.TYPE_NULL);
        item2 = (EditText) getActivity().findViewById(R.id.item2);
        item2.setInputType(InputType.TYPE_NULL);

        tv_feedback = (TextView) getActivity().findViewById(R.id.tv_feedback);

        key = (Button) getActivity().findViewById(R.id.btn_send_request);

        building = (Spinner) getActivity().findViewById(R.id.spinner);
        floor = (Spinner) getActivity().findViewById(R.id.spinner1);
        classroom = (Spinner) getActivity().findViewById(R.id.spinner2);

        building.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, buildingdata));
        floor.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, floordata));
        classroom.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, classroomdata));

        building.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str = buildingdata[position];
                classbar[0] = str;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        floor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str1 = floordata[position];
                classbar[1] = str1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        classroom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String str2 = classroomdata[position];
                classbar[2] = str2;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        key.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mClassroom = classbar[0] + classbar[1] + classbar[2];
                new Thread(new MyThread()).start();
            }
        });

        item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickDialog(DateType.TYPE_YMDHM);

            }
        });
        item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDatePickDialog(DateType.TYPE_YMDHM);

            }
        });
    }
    private void showDatePickDialog(DateType type) {
        DatePickDialog dialog = new DatePickDialog(getActivity());
        //设置上下年分限制
        dialog.setYearLimt(5);
        //设置标题
        dialog.setTitle("选择时间");
        //设置类型
        dialog.setType(type);
        //设置消息体的显示格式，日期格式
        dialog.setMessageFormat("yyyy-MM-dd HH:mm");
        //设置选择回调
        dialog.setOnChangeLisener(null);
        //设置点击确定按钮回调
        dialog.setOnSureLisener(null);
        dialog.show();
    }

}




