package adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.example.mrnobody43.shedule_application.MainSchedule;

import java.util.ArrayList;

import Utils.Constants;
import fragments.MainScheduleFragment;
import model.ClassRoom.WeekClassRoom;
import model.Group.WeekGroup;
import model.Teacher.WeekTeacher;

/**
 * Created by Mr.Nobody43 on 13.02.2018.
 */

public class MainScheduleFragmentAdapter extends FragmentStatePagerAdapter {

    public MainScheduleFragmentAdapter(FragmentManager fm) {
        super(fm);

        _day_of_a_weak = new ArrayList<String>();
        _day_of_a_weak.add(Constants.MONDAY);
        _day_of_a_weak.add(Constants.TUESDAY);
        _day_of_a_weak.add(Constants.WEDNESDAY);
        _day_of_a_weak.add(Constants.THURSDAY);
        _day_of_a_weak.add(Constants.FRIDAY);
        _day_of_a_weak.add(Constants.SATURDAY);
        _day_of_a_weak.add(Constants.SUNDAY);
    }

    @Override
    public Fragment getItem(int position) {

        switch (_CURRENT_STATE)
        {
            case Constants.GROUP: {
                return MainScheduleFragment.newInstance(ctx,  _currentScheduleGroup, position, _CURRENT_STATE, 1);
            }
            case Constants.TEACHER: {
                return MainScheduleFragment.newInstance(ctx,  _currentScheduleTeacher, position, _CURRENT_STATE, 1);////////////
            }
            case Constants.CLASSROOM: {
                return MainScheduleFragment.newInstance(ctx,  _currentScheduleGroup, position, _CURRENT_STATE, 1);/////////////
            }
            default: {
                break;
            }
        }
        return MainScheduleFragment.newInstance(ctx,  _currentScheduleGroup, position, _CURRENT_STATE, 1);
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return _day_of_a_weak.get(position);
    }

    public void set_CURRENT_STATE(Integer _CURRENT_STATE) {
        this._CURRENT_STATE = _CURRENT_STATE;
    }

    @Override
    public int getCount() {
        return Constants.DAYS_ON_WEEK;
    }


    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public void set_currentSchedule(WeekGroup _currentSchedule) {
        this._currentScheduleGroup = _currentSchedule;
    }

    public void set_currentSchedule(WeekTeacher _currentSchedule) {
        this._currentScheduleTeacher = _currentSchedule;
    }

    public void set_currentSchedule(WeekClassRoom _currentSchedule) {
        this._currentScheduleClassRoom = _currentSchedule;
    }

    public void setCtx(MainSchedule ctx) {
        this.ctx = ctx;
    }

    private WeekGroup _currentScheduleGroup;
    private WeekTeacher _currentScheduleTeacher;
    private WeekClassRoom _currentScheduleClassRoom;
    private ArrayList<String>_day_of_a_weak;
    private Integer _CURRENT_STATE;
    private MainSchedule ctx;
}