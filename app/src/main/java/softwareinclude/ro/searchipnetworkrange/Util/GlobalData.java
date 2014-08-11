package softwareinclude.ro.searchipnetworkrange.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by manolescusebastian on 8/11/14.
 */
public enum GlobalData {

    Data;

    public List<String> foundIPList = new ArrayList<String>();


    public List<String> getFoundIPList() {
        return foundIPList;
    }

    public void setFoundIPList(List<String> foundIPList) {
        this.foundIPList = foundIPList;
    }

}
