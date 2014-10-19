package barxdroid.wearabledatalayer;

import android.content.Intent;

import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.Author;
import anywheresoftware.b4a.BA.Events;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.objects.IntentWrapper;
import anywheresoftware.b4a.objects.collections.Map;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

@Author("BarxDroid")
@BA.Hide
public class ListenerService extends WearableListenerService{
	private static final String INTENT_ACTION = "barxdroid_wearlistener";
	private static final String B4A_SERVICE = ".wearlistenerservice";
	private static Map ChangedItemMap;
	private static Map DeletedItemMap;
	public WearableListener wl;
	
	public void onCreate() {
		super.onCreate();
		BA.Log("Internal ListenerService created");
		try {
			Class.forName(getPackageName() + B4A_SERVICE);
		}
		catch(ClassNotFoundException e) {
			BA.Log("WearListenerService not found.");
			BA.Log("Create a Service module called WearListenerService");
		}
	}
	
	/**
	 * Wearable Listener is used to access the messages and data changed on the Wear network when implementing Static listeners
	 */
	@ShortName("WearableListener")
	@Events(values={"MessageReceived(SourceNodeID As String, RequestID As Int, msgPath As String, Data As String)", "DataChanged(ChangedItems As Map, DeletedItems As Map)"})
	public static class WearableListener {
		@Hide
		public BA mBA;
		@Hide
		public String mEventname;
		
		/**
		 * initilizes the object and set the EventName for callback events
		 */
		public void Initialize(BA ba, String EventName) {
			mBA = ba;
			mEventname = EventName.toLowerCase(BA.cul);
		}
		
		/**
		 * Used to handle the starting intent when using static listeners.
		 * Will call the following events:
		 * 
		 * _MessageReceived - When a message is received
		 * _DataChanged - When a DataMap is changed on the Wear Network
		 */
		public boolean HandleIntent(IntentWrapper StartingIntent) {
			if (!StartingIntent.IsInitialized()) {
				return false;
			}
			
			if (StartingIntent.getAction().equals("barxdroid_wearlistener")) {
				String event = (String)StartingIntent.GetExtra("event");
				if (event.equals("messageReceived")) {
					String sourceNodeID = (String)StartingIntent.GetExtra("SourceNodeID");
					int requestID = (Integer)StartingIntent.GetExtra("RequestID");
					String msgPath = (String)StartingIntent.GetExtra("msgPath");
					String data = (String)StartingIntent.GetExtra("Data");
					if (mBA.subExists(mEventname + "_messagereceived")) {
						mBA.raiseEvent(this, mEventname + "_messagereceived", new Object[] {sourceNodeID, requestID, msgPath, data});
					}
				} else if (event.equals("dataChanged")) {
					Map ChangedItems = new Map();
					Map DeletedItems = new Map();
					if (StartingIntent.HasExtra("ChangedItems")) {
						ChangedItems = ChangedItemMap; 
					}
					if (StartingIntent.HasExtra("DeletedItems")) {
						DeletedItems = DeletedItemMap;
					}
					if (mBA.subExists(mEventname + "_datachanged")) {
						mBA.raiseEvent(this, mEventname + "_datachanged", new Object[] {ChangedItems, DeletedItems});
					}
				}
				return true;
			}
			return false;
		}
	}
	
	@Override
    public void onMessageReceived(MessageEvent message) {
		try {
			Intent intent;
			intent = new Intent(this, Class.forName(getPackageName() + B4A_SERVICE));
			intent.setAction(INTENT_ACTION);
			intent.putExtra("event", "messageReceived");
			intent.putExtra("SourceNodeID", message.getSourceNodeId());
			intent.putExtra("RequestID", message.getRequestId());
			intent.putExtra("msgPath", message.getPath());
			intent.putExtra("Data", new String(message.getData()));
			startService(intent);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
    }
	
	@Override
	public void onDataChanged(DataEventBuffer events) {
		Map changedItems = new Map();
		Map deletedItems = new Map();
		changedItems.Initialize();
		deletedItems.Initialize();
		for (DataEvent event : events) {
			if (event.getType() == DataEvent.TYPE_CHANGED) {
				DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
				changedItems.Put(dataMapItem.getUri().getPath(), dataMapItem.getDataMap());
			} else if (event.getType() == DataEvent.TYPE_DELETED) {
				DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
				deletedItems.Put(dataMapItem.getUri().getPath(), dataMapItem.getDataMap());
			}
		}
		
		try {
			Intent intent;
			intent = new Intent(this, Class.forName(getPackageName() + B4A_SERVICE));
			intent.setAction(INTENT_ACTION);
			intent.putExtra("event", "dataChanged");
			intent.putExtra("success", events.getStatus().isSuccess());
			if (changedItems.getSize() > 0) {
				ChangedItemMap = changedItems;
				intent.putExtra("ChangedItems", changedItems.getSize());
			}
			if (deletedItems.getSize() > 0) {
				DeletedItemMap = deletedItems;
				intent.putExtra("DeletedItems", deletedItems.getSize());
			}
			startService(intent);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}		
	}
}
