package com.paulvarry.intra42.utils;

import android.support.annotation.Nullable;

import com.google.gson.annotations.SerializedName;
import com.paulvarry.intra42.api.model.ScaleTeams;
import com.paulvarry.intra42.api.model.Slots;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SlotsTools {

    public List<SlotsDay> slots;

    public SlotsTools(List<Slots> slotsList) {
        HashMap<Date, SlotsDay> map = new HashMap<>();

        for (Slots s : slotsList) {

            if (DateTool.isInFuture(s.endAt)) {
                boolean isAdded = false;
                for (Map.Entry<Date, SlotsDay> entry : map.entrySet()) {
                    Date key = entry.getKey();
                    SlotsDay value = entry.getValue();

                    if (DateTool.sameDayOf(s.beginAt, key)) {
                        value.add(s);
                        isAdded = true;
                        break;
                    }
                }

                if (!isAdded)
                    map.put(s.beginAt, new SlotsDay(s));
            }
        }

        slots = new ArrayList<>(map.values());
        Collections.sort(slots, new Comparator<SlotsDay>() {
            public int compare(SlotsDay emp1, SlotsDay emp2) {
                return emp1.day.compareTo(emp2.day);
            }
        });
    }

    static public class SlotsGroup {

        @SerializedName("begin_at")
        public Date beginAt;
        @SerializedName("end_at")
        public Date endAt;
        @Nullable
        @SerializedName("slots")
        public List<Slots> group;
        @Nullable
        @SerializedName("scale_team")
        public ScaleTeams scaleTeam;
        @SerializedName("is_booked")
        public boolean isBooked;

        public SlotsGroup(Slots s) {
            beginAt = s.beginAt;
            endAt = s.endAt;
            group = new ArrayList<>();
            group.add(s);
            scaleTeam = s.scaleTeam;
            isBooked = s.isBooked;
        }

        public SlotsGroup() {
            scaleTeam = null;
            beginAt = new Date();
            endAt = new Date();
        }

        void addAfter(Slots slots) {
            group.add(slots);
            endAt = slots.endAt;
            scaleTeam = slots.scaleTeam;
        }

        void addBefore(Slots slots) {
            group.add(slots);
            beginAt = slots.beginAt;
            scaleTeam = slots.scaleTeam;
        }
    }

    public class SlotsDay {
        public List<SlotsGroup> slots;
        public Date day;

        public SlotsDay(Slots s) {
            slots = new ArrayList<>();
            slots.add(new SlotsGroup(s));
            day = s.beginAt;
        }

        public void add(Slots slots) {
            for (SlotsGroup slotsGroup : this.slots) {
                if (((slots.scaleTeam == null && slotsGroup.scaleTeam == null) ||
                        (slots.scaleTeam != null && slotsGroup.scaleTeam != null && slots.scaleTeam.equals(slotsGroup.scaleTeam))) &&
                        (slots.isBooked == slotsGroup.isBooked)) {
                    if (slotsGroup.endAt.compareTo(slots.beginAt) == 0) {
                        slotsGroup.addAfter(slots);
                        return;
                    } else if (slotsGroup.beginAt.compareTo(slots.endAt) == 0) {
                        slotsGroup.addBefore(slots);
                        return;
                    }
                }
            }
            this.slots.add(new SlotsGroup(slots));
            day = slots.beginAt;
        }
    }

}
