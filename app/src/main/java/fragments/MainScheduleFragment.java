package fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.mrnobody43.shedule_application.MainSchedule;
import com.example.mrnobody43.shedule_application.R;

import Utils.Constants;
import adapters.ScheduleClassRoomAdapter;
import adapters.ScheduleEmptyAdapter;
import adapters.ScheduleGroupAdapter;
import adapters.ScheduleTeacherAdapter;
import model.ClassRoom.WeekClassRoom;
import model.Group.WeekGroup;
import model.Teacher.WeekTeacher;

/**
 * Created by Mr.Nobody43 on 13.02.2018.
 */

public class MainScheduleFragment extends Fragment {

    static public MainScheduleFragment newInstance(MainSchedule ctx, WeekGroup currentScheduleGroup, int day, int CURRENT_STATE, int week) {
        MainScheduleFragment pageFragment = new MainScheduleFragment();

        pageFragment.set_CURRENT_STATE(CURRENT_STATE);
        pageFragment._scheduleGroupAdapter = new ScheduleGroupAdapter(ctx,currentScheduleGroup, day, week);
        pageFragment._ctx = ctx;

        return pageFragment;
    }

    static public MainScheduleFragment newInstance(MainSchedule ctx, WeekTeacher currentScheduleTeacher, int day, int CURRENT_STATE, int week) {
        MainScheduleFragment pageFragment = new MainScheduleFragment();

        pageFragment.set_CURRENT_STATE(CURRENT_STATE);
        pageFragment._scheduleTeacherAdapter = new ScheduleTeacherAdapter(ctx, currentScheduleTeacher, day, week);
        pageFragment._ctx = ctx;

        return pageFragment;
    }

    static public MainScheduleFragment newInstance(MainSchedule ctx, WeekClassRoom currentScheduleClassroom, int day, int CURRENT_STATE, int week) {
        MainScheduleFragment pageFragment = new MainScheduleFragment();

        pageFragment.set_CURRENT_STATE(CURRENT_STATE);
        pageFragment._scheduleClassRoomAdapter = new ScheduleClassRoomAdapter(ctx, currentScheduleClassroom, day, week);
        pageFragment._ctx = ctx;

        return pageFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view  = inflater.inflate(R.layout.fragment_schedule, container, false);

        ListView listView = (ListView) view.findViewById(R.id.list1);

        SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeContainer);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()  {
                _ctx.renderScheduleData();
            }
        });

        if(_CURRENT_STATE != null) {
            switch (_CURRENT_STATE) {
                case Constants.GROUP: {
                    if (_scheduleGroupAdapter != null && !_scheduleGroupAdapter.isNull()) {
                        listView.setAdapter(_scheduleGroupAdapter);
                    } else {
                        listView.setAdapter(new ScheduleEmptyAdapter(getContext()));
                    }
                    break;
                }
                case Constants.TEACHER: {
                    if (_scheduleTeacherAdapter != null && !_scheduleTeacherAdapter.isNull()) {
                        listView.setAdapter(_scheduleTeacherAdapter);
                    } else {
                        listView.setAdapter(new ScheduleEmptyAdapter(getContext()));
                    }
                    break;
                }
                case Constants.CLASSROOM: {
                    if (_scheduleClassRoomAdapter != null && !_scheduleClassRoomAdapter.isNull()) {
                        listView.setAdapter(_scheduleClassRoomAdapter);
                    } else {
                        listView.setAdapter(new ScheduleEmptyAdapter(getContext()));
                    }
                    break;
                }
                default: {
                    break;
                }
            }
        }

        //swipeRefreshLayout.setRefreshing(false);
        return view;
    }

    public void set_CURRENT_STATE(Integer _CURRENT_STATE) {
        this._CURRENT_STATE = _CURRENT_STATE;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    private MainSchedule _ctx;
    private Integer _CURRENT_STATE;
    private ScheduleClassRoomAdapter _scheduleClassRoomAdapter;
    private ScheduleTeacherAdapter _scheduleTeacherAdapter;
    private ScheduleGroupAdapter _scheduleGroupAdapter;
}
