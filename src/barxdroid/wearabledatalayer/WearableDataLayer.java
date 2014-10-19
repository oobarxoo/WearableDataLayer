package barxdroid.wearabledatalayer;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Binder;
import android.os.Bundle;
import anywheresoftware.b4a.AbsObjectWrapper;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.Author;
import anywheresoftware.b4a.BA.DependsOn;
import anywheresoftware.b4a.BA.Events;
import anywheresoftware.b4a.BA.Hide;
import anywheresoftware.b4a.BA.Permissions;
import anywheresoftware.b4a.BA.ShortName;
import anywheresoftware.b4a.BA.Version;
import anywheresoftware.b4a.objects.collections.List;
import anywheresoftware.b4a.objects.collections.Map;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.data.FreezableUtils;
import com.google.android.gms.wearable.Asset;
import com.google.android.gms.wearable.DataApi;
import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.DataItem;
import com.google.android.gms.wearable.DataItemBuffer;
import com.google.android.gms.wearable.DataMap;
import com.google.android.gms.wearable.DataMapItem;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageApi.SendMessageResult;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.PutDataMapRequest;
import com.google.android.gms.wearable.Wearable;

@DependsOn(values = {"android-support-v4", "google-play-services"})
@Permissions(values = {})
@ShortName("WearableDataLayer")
@Author("BarxDroid")
@Version(0.98F)
@Events(values={"Connected()", "ConnectionSuspended(Reason As String)", "ConnectionFailed(ErrorCode As Int, Reason As String)", "PeerConnected(ID As Int, DisplayName As String)", "MessageSent(Success As Boolean)", "MessageReceived(SourceNodeID As String, RequestID As Int, msgPath As String, Data As String)", "DataMapAdded(Success As Boolean)", "DataMapResults(Success As Boolean, Results As Map)", "DataMapDeleted(Success As Boolean)", "NodeResults(Results As List)", "LocalNodeIDResult(Success As Boolean, NodeID As String, NodeDisplayName As String)", "DataChanged(ChangedItems As Map, DeletedItems As Map)", "BitmapResult(Result As Bitmap)"})

public class WearableDataLayer implements DataApi.DataListener, NodeApi.NodeListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

	private static GoogleApiClient mGoogleApiClient;
	private static String mEventname;
	private static BA mBA;
	private static boolean mDataListenerRegistered = false;
	private static boolean mMessageListenerRegistered = false;
	private static boolean mNodeListenerRegistered = false;
	private long CallingIDToken;
	
	public Message message = new Message();
	
	/**
	 * Initializes the object.
	 * 
	 * Note: this library requires Android 4.3 (API18) or above

	 */
	public void Initialize(final BA ba, String Eventname) {
		mBA = ba;
		mEventname = Eventname.toLowerCase(BA.cul);
		mGoogleApiClient = new GoogleApiClient.Builder(ba.context)
			.addConnectionCallbacks(this)
			.addOnConnectionFailedListener(this)
			.addApi(Wearable.API)
			.build();
	}
	
	/**
	 * Connects the Google Play services client (required for the Data Layer to work)
	 * 
	 * The _onConnected event will raise once the connection is successful.
	 * 
	 * Do NOT attempt to use the Data Layer until the connection is successful
	 * 
	 * Make sure you disconnect the client once done (probably best to do this in Activity_Pause()
	 */
	public void Connect() {
		mGoogleApiClient.connect();
	}
	
	@Hide
	@Override
	public void onConnected(Bundle connectionHint) {
		BA.Log("Google Play Services Client Connected");
		
		Wearable.DataApi.addListener(mGoogleApiClient, this);
		mDataListenerRegistered = true;
		Wearable.NodeApi.addListener(mGoogleApiClient, this);
		mNodeListenerRegistered = true;
		BA.Log("DataListener and NodeListener registered");
		
		if (mBA.subExists(mEventname + "_connected")) {		
			mBA.raiseEvent(mGoogleApiClient, mEventname + "_connected");
		}
	}
	
	@Hide
	@Override
	public void onConnectionSuspended(int cause) {
		BA.Log("Google Play Services Client Suspended:");
		if (mBA.subExists(mEventname + "_connectionsuspended")) {
			String strCause = "";
			if (cause == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
				strCause = "Network Lost";
			}
			if (cause == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
				strCause = "Service Disconnected";
			}
			BA.Log(" - " + strCause);
			mBA.raiseEvent(mGoogleApiClient, mEventname + "_connectionsuspended", new Object[] {strCause});
		}
	}
	
	@Hide
	@Override
	public void onConnectionFailed(ConnectionResult result) {
		BA.Log("Google Play Services Client Connection Failed");
		BA.Log("Failed" + result.getErrorCode());
		if (mBA.subExists(mEventname + "_connectionfailed")) {
			
			String reason;
			
			switch(result.getErrorCode()) {
				case 16:
					reason = "The Google Play Services API is not available";
					break;
				case 13:
					reason = "The connection was cancelled by calling disconnect()";
					break;
				case 12:
					reason = "The device date is likely to be wrong";
					break;
				case 10:
					reason = "Developer Error - The application is misconfigured";
					break;
				case 8:
					reason = "An internal error has occurred. Retrying should resolve the problem";
					break;
				case 15:
					reason = "An interrupt occurred while waiting for the connection complete";
					break;
				case 5:
					reason = "The client attempted to connect to the service with an invalid account name specified";
					break;
				case 11:
					reason = "The application is not licensed to the user";
					break;
				case 7:
					reason = "A network error occurred. Retrying should solve the problem";
					break;
				case 6:
					reason = "Resolution required";
					break;
				case 3:
					reason = "The installed version of Google Play Services has been disabled on this device.";
					//pass to getErrorDialog
					break;
				case 9:
					reason = "The version of Google Play Services installed on this device is not authentic";
					break;
				case 1:
					reason = "Google Play Services is missing on this device";
					//pass to getErrorDialog
					break;
				case 2:
					reason = "The installed version of Google Play Services is out of date";
					//pass to getErrorDialog
					break;
				case 4:
					reason = "The client attempted to login to the service but the user is not signed in.";
					break;
				case 0:
					reason = "The connection was successful";
					break;
				case 14:
					reason = "The timeout was exceeded while waiting for the connection to complete.";
					break;
				default:
					reason = "Undefined Error";
					break;
			}
			mBA.raiseEvent(mGoogleApiClient, mEventname + "_connectionfailed", new Object[] {result.getErrorCode(), reason});
		}
	}
	
	/**
	 * Disconnects the Google Play services client
	 * 
	 * Should always be called once you have finished with the Data Layer e.g. when the app closes or is paused.
	 */
	public void Disconnect(BA ba) {
		if (mDataListenerRegistered) {
			Wearable.DataApi.removeListener(mGoogleApiClient, this);
		}
		
		if (mMessageListenerRegistered) {
			message.RemoveListener();
		}
		
		if (mNodeListenerRegistered) {
			Wearable.NodeApi.removeListener(mGoogleApiClient, this);
		}
		mGoogleApiClient.disconnect();
	}
	
	/**
	 * Adds a Data Map to the Client to sync across the Wearable Data connection.
	 * DataMaps are synchronized across all devices
	 * Path - The path to store the DataMap under. e.g "/User"
	 * DataMap - The DataMap object to add
	 */
	public void AddDataMap(String Path, DataMap dataMap) {
		
		String path = Path;
		if (!Path.startsWith("/")) {
			path = "/" + Path; 
		}
		
		PutDataMapRequest dataMapRequest = PutDataMapRequest.create(path);
		dataMapRequest.getDataMap().putAll(dataMap);
		PutDataRequest request = dataMapRequest.asPutDataRequest(); 
		Wearable.DataApi
			.putDataItem(mGoogleApiClient, request)
			.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
				@Override
				public void onResult(DataApi.DataItemResult dataItemResult) {
					boolean success = false;
					if (dataItemResult.getStatus().isSuccess()) {
						success = true;
					}
					if (mBA.subExists(mEventname + "_datamapadded")) {
						mBA.raiseEventFromDifferentThread(mGoogleApiClient, null, 0, mEventname + "_datamapadded", false, new Object[] {success});			
					} else {
						BA.Log("Data Map Added: " + success);
					}	
				}
			});
	}
	
	@Hide
	@Override
	public void onDataChanged(DataEventBuffer dataEvents) {
		final java.util.List<DataEvent> events = FreezableUtils.freezeIterable(dataEvents);
		dataEvents.close();
		if (mBA.subExists(mEventname + "_datachanged")) {
			Map ChangedItems = new Map();
			Map DeletedItems = new Map();
			ChangedItems.Initialize();
			DeletedItems.Initialize();
			
			for (DataEvent event : events) {
				if (event.getType() == DataEvent.TYPE_CHANGED) {
					DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
					ChangedItems.Put(dataMapItem.getUri().getPath(), dataMapItem.getDataMap());
				} else if (event.getType() == DataEvent.TYPE_DELETED) {
					DataMapItem dataMapItem = DataMapItem.fromDataItem(event.getDataItem());
					DeletedItems.Put(dataMapItem.getUri().getPath(), dataMapItem.getDataMap());
				}
			}
			mBA.raiseEventFromDifferentThread(mGoogleApiClient, null, 0, mEventname + "_datachanged", false,  new Object[] {ChangedItems, DeletedItems});
		}
	}
	
	/**
	 * Deletes a DataMap
	 * 
	 * Path - The path which the DataMap resides.
	 */
	public void DeleteDataMap(String Path) {
		BA.Log("Deleting " + createUri(Path).toString());
		Wearable.DataApi	
			.deleteDataItems(mGoogleApiClient, createUri(Path))
			.setResultCallback(new ResultCallback<DataApi.DeleteDataItemsResult>() {
				@Override
				public void onResult(DataApi.DeleteDataItemsResult deleteDataItemResult) {
					boolean success = false;
					if (deleteDataItemResult.getStatus().isSuccess()) {
						success = true;
					}
					if (mBA.subExists(mEventname + "_datamapdeleted")) {
						mBA.raiseEventFromDifferentThread(mGoogleApiClient, null, 0, mEventname + "_datamapdeleted", false, new Object[] {success});
					} else {
						BA.Log("Data Map Deleted: " + success);
					}
				}
			});
	}

	/**
	 * Gets an existing DataMap
	 * 
	 * Path - the path that the DataMap resides
	 * Tag - a tag that is passed through to the results to make the result set identifiable
	 * 
	 * If there is more than one DataMap with the same Path present on the Wear network. e.g. from different Nodes.
	 * All the DataMaps with that name will be returned.
	 * 
	 * Use GetDataMap2 to specify a Node to narrow down a specific DataMap.
	 */
	public void GetDataMap(String Path, String Tag) {
		GetDataMap2(null, Path, Tag);
	}
	
	/**
	 * Similar to GetDataMap but allows you to specify a NodeID. 
	 */
	public void GetDataMap2(String NodeID, String Path, String Tag) {
		Uri uri;
		if (null == NodeID) {
			uri = createUri(Path);
		} else {
			uri = createUri(NodeID, Path);
		}
		Wearable.DataApi
			.getDataItem(mGoogleApiClient, uri)
			.setResultCallback(new ResultCallback<DataApi.DataItemResult>() {
				@Override
				public void onResult(DataApi.DataItemResult dataItemResult) {					
					boolean success = dataItemResult.getStatus().isSuccess();
					if (mBA.subExists(mEventname + "_datamapresults")) {
						if (success) {
							Map result = new Map();
							result.Initialize();
							result.Put(dataItemResult.getDataItem().getUri(), DataMapItem.fromDataItem(dataItemResult.getDataItem()).getDataMap());
						
							mBA.raiseEventFromDifferentThread(mGoogleApiClient, null, 0, mEventname + "_datamapresults", false, new Object[] {success, result});
						} else {
							mBA.raiseEventFromDifferentThread(mGoogleApiClient, null, 0, mEventname + "_datamapresults", false, new Object[] {success, null});
						}
					} else {
						BA.Log("Data Map found: " + success);
					}
				}
		});
	}

	/**
	 * Returns all the present DataMaps as a Map.
	 * 
	 * The results will be returned in the DataLayer_DataMapResults event
	 * 
	 * Each Map Key-Value pair is as follows
	 *     Key - The path that the DataMap resides
	 *     Value - The DataMap object
	 */
	public void GetAllDataMaps() {
		Wearable.DataApi
			.getDataItems(mGoogleApiClient)
			.setResultCallback(new ResultCallback<DataItemBuffer>() {
				@Override
				public void onResult(DataItemBuffer dataEvents) {
					final java.util.List<DataItem> items = FreezableUtils.freezeIterable(dataEvents);
					boolean success = dataEvents.getStatus().isSuccess();
					if (mBA.subExists(mEventname + "_datamapresults")) {
						if (success){
							Map results = new Map();
							results.Initialize();
							for (DataItem item : items) {
								results.Put(item.getUri(), DataMapItem.fromDataItem(item).getDataMap());
							}
							mBA.raiseEventFromDifferentThread(mGoogleApiClient, null, 0, mEventname + "_datamapresults", false, new Object[] {success, results});
						} else {
							mBA.raiseEventFromDifferentThread(mGoogleApiClient, null, 0, mEventname, false, new Object[] {success, null});
						}
					} else {
						if (success) {
							BA.Log("Data Maps found: " + items.size());
						}
					}
				}	
			});
	}
	
	/**
	 * Gets the NodeID of the local Device
	 */
	public void LocalNodeID() {
		Wearable.NodeApi
			.getLocalNode(mGoogleApiClient)
			.setResultCallback(new ResultCallback<NodeApi.GetLocalNodeResult>() {
				@Override
				public void onResult(NodeApi.GetLocalNodeResult localNode) {
					boolean success = localNode.getStatus().isSuccess();
					if (success) {
						if (mBA.subExists(mEventname + "_localnodeidresult")) {
							mBA.raiseEventFromDifferentThread(mGoogleApiClient, null, 0, mEventname + "_localnodeidresult", false, new Object[] {success, localNode.getNode().getId(), localNode.getNode().getDisplayName()});
						}
					} else {
						if (mBA.subExists(mEventname + "_localnodeidresult")) {
							mBA.raiseEventFromDifferentThread(mGoogleApiClient, null, 0, mEventname + "_localnodeidresult", false, new Object[] {success, null});
						} else {
							BA.Log("LocalNodeIDResult: Failed");
						}
					}
				}
			});
	}
	
	/**
	 * Call this to get a list of the connected Nodes (Devices)
	 * 
	 * The returned List will contains a Map for each node. 
	 * The map will then contains 3 Key-Value pairs:
	 *     ID - The ID of the node, this is used to reference the node when sending messages etc
	 *     DisplayName - A HumanReadable name for the device (on my Samsung Gear Live this matched the ID so was of no use
	 *     ToString - A string representation of the full Node object, used mainly for my testing 
	 */
	public void GetConnectedNodes() {
				
		PendingResult<NodeApi.GetConnectedNodesResult> nodes = Wearable.NodeApi.getConnectedNodes(mGoogleApiClient);
		nodes.setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
			@Override
			public void onResult(NodeApi.GetConnectedNodesResult result) {
				List nodes = new List();
				nodes.Initialize();
				for (int i = 0; i < result.getNodes().size(); i++) {
					Map node = new Map();
					node.Initialize();
					Node mNode = result.getNodes().get(i);
					node.Put("ID", mNode.getId());
					node.Put("DisplayName", mNode.getDisplayName());
					node.Put("toString", mNode.toString());
					nodes.Add(node.getObject());
				}
				
				if (mBA.subExists(mEventname + "_noderesults")) {
					mBA.raiseEvent(mGoogleApiClient, mEventname + "_noderesults", new Object[] {nodes});
				}
			}
		});
	}
	
	public void ClearCallingIdentity() {
		CallingIDToken = Binder.clearCallingIdentity(); 
	}
	
	public void RestoreCallingIdentity() {
		Binder.restoreCallingIdentity(CallingIDToken);
	}
	
	/**
	 * Gets a Bitmap from an Asset
	 * 
	 * Result returned in BitMapResult()
	 */
	public void GetBitmapFromAsset(final String tag, Asset asset) {
		if (asset == null) {
			throw new IllegalArgumentException("Asset cannot be null");
		}
		
		PendingResult<DataApi.GetFdForAssetResult> pendingResult = Wearable.DataApi.getFdForAsset(mGoogleApiClient, asset);
		pendingResult.setResultCallback(new ResultCallback<DataApi.GetFdForAssetResult>() {
			@Override
			public void onResult(DataApi.GetFdForAssetResult result) {
				InputStream assetInputStream;
				assetInputStream = result.getInputStream();
				if (assetInputStream != null) {
					Bitmap bitmapResult;
					bitmapResult = BitmapFactory.decodeStream(assetInputStream);
					if (mBA.subExists(mEventname + "_bitmapresult")) {
						mBA.raiseEvent(mGoogleApiClient, mEventname + "_bitmapresult", new Object[] {tag, bitmapResult});
					}
				}
			}
		});
	}
	
	/**
	 * Gets a File from an Asset
	 * 
	 * NOT WORKING YET!
	 */
	public void GetFileFromAsset(Asset asset, String TargetDir, String TargetFilename) {
		if (asset == null) {
			throw new IllegalArgumentException("Asset cannot be null");
		}
		// ToDo
		//InputStream assetInputStream = Wearable.DataApi.getFdForAsset(mGoogleApiClient, asset).await().getInputStream();
			
	}
	
	public static class Message extends AbsObjectWrapper<MessageApi> implements MessageApi.MessageListener{
		/**
		 * NOTE: This method doesn't do anything and is to provide information only.
		 * 
		 * A Message is used to 'Send and forget' small amounts (&lt;100KB) of data.
		 * 
		 * You can receive the messages either with dynamic or static receivers. 
		 * 
		 * Dynamic - Will only be received while the listener is registered. 
		 * 		Register with .AddDynamicReceiver.
		 * 		You must also unregister once done using .RemoveDynamicReceiver 
		 * 
		 * static - More complex but means you can receive messages at any time
		 * 		Add the following text to the Manifest Editor
		 * 		<code>AddApplicationText(    
		 * <service android:name="barxdroid.wearabledatalayer.ListenerService"
		 * 	android:label="Wearable Listener">
  		 * 		<intent-filter>
  		 * 			<action android:name="com.google.android.gms.wearable.BIND_LISTENER" />
  		 * 		</intent-filter>
		 * </service>)
		 * 		</code>
		 * 		Then add a service called 'WearListenerService to your project and in the service module get the message with the following sub.
		 */
		public void Info() {
			
		}
		
		/**
		 * Sends a message to a specified node
		 * NodeID - The ID of the node to connect to.
		 * Timeout - The timeout before the message sending will fail in milliseconds.
		 * Path - Denotes a path identifier to specify a particular endpoint at the receiving node.
		 * Data - A ByteArray of data to pass. Do not pass >100KB. Pass Null if not required.
		 */
		public void Send(final String NodeID, final long Timeout, final String msgPath, final String Data) {
			PendingResult<MessageApi.SendMessageResult> results = Wearable.MessageApi.sendMessage(mGoogleApiClient, NodeID, msgPath, Data.getBytes());
			results.setResultCallback(new ResultCallback<MessageApi.SendMessageResult>() {

				@Override
				public void onResult(SendMessageResult msgResult) {
					boolean Success = false;
					
					if (msgResult.getStatus().isSuccess()) {
							Success = true;
					}
					if (mBA.subExists(mEventname + "_messagesent")) {
						mBA.raiseEventFromDifferentThread(mGoogleApiClient, null, 0, mEventname + "_messagesent", false, new Object[] {Success});					
					} else {
						BA.Log("Message Sent: " + Success);
					}
				}	
			}, Timeout, TimeUnit.MILLISECONDS);
		}
		
		/**
		 * Adds a dynamic listener to receive message events. 
		 * A dynamic listener is create at runtime and will be removed once the process is stopped or once RemoveListener is called.
		 */
		public void AddDynamicListener() {
			Wearable.MessageApi.addListener(mGoogleApiClient, this);
			BA.Log("Dynamic Message Listener Started");
		}
		
		/**
		 * Removes the dynamic listener so no more messages will be received
		 */
		public void RemoveListener() {
			Wearable.MessageApi.removeListener(mGoogleApiClient, this);
			BA.Log("Dynamic Message Listener Stopped");
		}

		@Hide
		@Override
		public void onMessageReceived(MessageEvent msg) {
			msg.getSourceNodeId();
			BA.Log("Dynamic Message Received");
			if (mBA.subExists(mEventname + "_messagereceived")) {
				BA.Log("MessageReceived Sub present");
				mBA.raiseEventFromDifferentThread(mGoogleApiClient, null, 0, mEventname + "_messagereceived", false, new Object[] {msg.getSourceNodeId(), msg.getRequestId(), msg.getPath(), new String(msg.getData())});
			}	
		}
	}

	@Hide
	@Override
	public void onPeerConnected(Node peer) {
		BA.Log("Connected to node" + peer.getDisplayName());
		if (mBA.subExists(mEventname + "_peerconnected")) {
			mBA.raiseEvent(mGoogleApiClient, mEventname + "_peerconnected", new Object[] {peer.getId(), peer.getDisplayName()});
		}				
	}

	@Hide
	@Override
	public void onPeerDisconnected(Node peer) {
		BA.Log("Disconnected from node" + peer.getDisplayName());
		if (mBA.subExists(mEventname + "_peerdisconnected")) {
			mBA.raiseEvent(mGoogleApiClient, mEventname + "peerdisconnected", new Object[] {peer.getId(), peer.getDisplayName()});
		}	
	}

	private Uri createUri(String path) {
		if (path.startsWith("wear://")) {
			return Uri.parse(path);
		}
		Uri uri = new Uri.Builder().scheme("wear").path(path).build();
		return uri;
	}
	
	private Uri createUri(String node, String path) {
		if (path.startsWith("wear://")) {
			return Uri.parse(path);
		}		
		Uri uri = new Uri.Builder().scheme("wear").authority(node).path(path).build();
		return uri;
	}
}