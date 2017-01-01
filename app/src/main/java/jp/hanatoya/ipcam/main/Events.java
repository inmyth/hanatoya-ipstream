package jp.hanatoya.ipcam.main;


import java.util.ArrayList;
import java.util.List;

import jp.hanatoya.ipcam.models.CamExt;
import jp.hanatoya.ipcam.repo.Switch;

public class Events {

    public static class SwitchToStream {
        long id;

        SwitchToStream(long id) {
            this.id = id;
        }
    }

    public static class SwitchToEdit {
        long id;

        SwitchToEdit(long id) {
            this.id = id;
        }
    }

    public static class AddNewCam{}

    public static class RequestBack{}

    public static class CameraPing{
        public boolean isOk;

        public CameraPing(boolean isOk) {
            this.isOk = isOk;
        }
    }


    public static class EditSwitch{
        public Switch s;

        public EditSwitch(Switch s) {
            this.s = s;
        }
    }

    public static class DeleteSwitch{
        public Switch s;

        public DeleteSwitch(Switch s){
            this.s = s;
        }

    }

    public static class SwitchUpdated{}

    public static class OpenCgiDialog{
        public CamExt camExt;

        public OpenCgiDialog(CamExt camExt) {
            this.camExt = camExt;
        }
    }

    public static class CgiClicked{
        public Switch s;

        public CgiClicked(Switch s) {
            this.s = s;
        }
    }

    public static class RequestFileImportInstructionDialog{}



}
