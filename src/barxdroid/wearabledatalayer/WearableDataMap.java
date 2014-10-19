package barxdroid.wearabledatalayer;

import anywheresoftware.b4a.AbsObjectWrapper;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BA.Author;
import anywheresoftware.b4a.BA.ShortName;

import com.google.android.gms.wearable.DataMap;

@ShortName("WearableDataMap")
@Author("BarxDroid")

public class WearableDataMap extends AbsObjectWrapper<DataMap>{
	
	/**
	 * Initializes the object.
	 * No Eventname is required. The DataLayer Eventname is used.
	 */
	public void Initialize(BA ba) {
		setObject(new DataMap());
	}
	
	/**
	 * Clears all previously added data items from the DataMap
	 */
	public void Clear() {
		((DataMap)getObject()).clear();
	}
	
	/**
	 * Checks if the given key is contained in the DataMap.
	 * 
	 * Returns True if the key is present
	 */
	public boolean ContainsKey(String Key) {
		return ((DataMap)getObject()).containsKey(Key);
	}
	
	/**
	 * Returns the DataMap entry with the given Key as an Object
	 */
	public Object Get(String Key) {
		return ((DataMap)getObject()).get(Key);
	}
	
	/**
	 * Gets an Asset data item from the DataMap
	 * 
	 * Key - The reference as set in in the PutAsset() method
	 */
	public com.google.android.gms.wearable.Asset GetAsset(String Key) {
		return ((DataMap)getObject()).getAsset(Key);
	}
	
	/**
	 * Gets a Boolean data item from the DataMap
	 * 
	 * Key - The reference as set in in the PutBoolean() method
	 */
	public Boolean GetBoolean(String Key) {
		return ((DataMap)getObject()).getBoolean(Key);
	}
	
	/**
	 * Gets a Byte data item from the DataMap
	 * 
	 * Key - The reference as set in in the PutByte() method
	 */
	public byte GetByte(String Key) {
		return ((DataMap)getObject()).getByte(Key);
	}
	
	/**
	 * Gets a Byte Array data item from the DataMap
	 * 
	 * Key - The reference as set in in the PutByteArray() method
	 */
	public byte[] GetByteArray(String Key) {
		return ((DataMap)getObject()).getByteArray(Key);
	}
	
	/**
	 * Gets a Double data item from the DataMap
	 * 
	 * Key - The reference as set in in the PutDouble() method
	 */
	public double GetDouble(String Key) {
		return ((DataMap)getObject()).getDouble(Key);
	}
	
	/**
	 * Gets a Float data item from the DataMap
	 * 
	 * Key - The reference as set in in the PutFloat() method
	 */
	public float GetFloat(String Key) {
		return ((DataMap)getObject()).getFloat(Key);
	}
	
	/**
	 * Gets a Float Array data item from the DataMap
	 * 
	 * Key - The reference as set in in the PutFloatArray() method
	 */
	public float[] GetFloatArray(String Key) {
		return ((DataMap)getObject()).getFloatArray(Key);
	}
	
	/**
	 * Gets an Int data item from the DataMap
	 * 
	 * Key - The reference as set in in the PutInt() method
	 */
	public int GetInt(String Key) {
		return ((DataMap)getObject()).getInt(Key);
	}
	
	/**
	 * Gets a Long data item from the DataMap
	 * 
	 * Key - The reference as set in in the PutLong() method
	 */
	public long GetLong(String Key) {
		return ((DataMap)getObject()).getLong(Key);
	}
	
	/**
	 * Gets a Long Array data item from the DataMap
	 * 
	 * Key - The reference as set in in the PutLongArray() method
	 */
	public long[] GetLongArray(String Key) {
		return ((DataMap)getObject()).getLongArray(Key);
	}
	
	/**
	 * Gets a String data item from the DataMap
	 * 
	 * Key - The reference as set in in the PutString() method
	 */
	
	public String GetString(String Key) {
		return ((DataMap)getObject()).getString(Key);
	}
	
	/**
	 * Gets a StringArray data item from the DataMap
	 * 
	 * Key - The reference as set in in the PutStringArray method
	 */
	
	public String[] GetStringArray(String Key) {
		return ((DataMap)getObject()).getStringArray(Key);
	}
	
	/**
	 * Return True if the DataMap is currently empty
	 */
	public boolean isEmpty() {
		return ((DataMap)getObject()).isEmpty();
	}
	
	/**
	 * Adds an Asset data item to the DataMap
	 * 
	 * Key - a key used to reference the data item
	 * Asset - the Asset object to pass.
	 */
	public void PutAsset(String Key, com.google.android.gms.wearable.Asset Asset) {
		((DataMap)getObject()).putAsset(Key, Asset);
	}
	
	/**
	 * Adds a Boolean data item to the DataMap
	 * 
	 * Key - a key used to reference the data item
	 * Val - the value to set to.
	 */
	public void PutBoolean(String Key, Boolean Val) {
		((DataMap)getObject()).putBoolean(Key, Val);
	}
	
	/**
	 * Adds a Byte data item to the DataMap
	 * 
	 * Key - a key used to reference the data item
	 * Val - the value to set to.
	 */
	public void PutByte(String Key, byte Val) {
		((DataMap)getObject()).putByte(Key, Val);
	}
	
	/**
	 * Adds a Byte Array data item to the DataMap
	 * 
	 * Key - a key used to reference the data item
	 * Val - the value to set to.
	 */
	public void PutByteArray(String Key, byte[] Val) {
		((DataMap)getObject()).putByteArray(Key, Val);
	}
	
	/**
	 * Adds a Double data item to the DataMap
	 * 
	 * Key - a key used to reference the data item
	 * Val - the value to set to.
	 */
	public void PutDouble(String Key, double Val) {
		((DataMap)getObject()).putDouble(Key, Val);
	}
	
	/**
	 * Adds a Float data item to the DataMap
	 * 
	 * Key - a key used to reference the data item
	 * Val - the value to set to.
	 */
	public void PutFloat(String Key, float Val) {
		((DataMap)getObject()).putFloat(Key, Val);
	}
	
	/**
	 * Adds a Float Array data item to the DataMap
	 * 
	 * Key - a key used to reference the data item
	 * Val - the value to set to.
	 */
	public void PutFloatArray(String Key, float[] Val) {
		((DataMap)getObject()).putFloatArray(Key, Val);
	}
	
	/**
	 * Adds an Int data item to the DataMap.
	 * 
	 * Key - a key use to reference the data item
	 * Val - the value to set to.
	 */
	public void PutInt(String Key, int Val) {
		((DataMap)getObject()).putInt(Key, Val);
	}
	
	/**
	 * Adds a Long data item to the DataMap
	 * 
	 * Key - a key used to reference the data item
	 * Val - the value to set to.
	 */
	public void PutLong(String Key, long Val) {
		((DataMap)getObject()).putLong(Key, Val);
	}
	
	/**
	 * Adds a Long Array data item to the DataMap
	 * 
	 * Key - a key used to reference the data item
	 * Val - the value to set to.
	 */
	public void PutLongArray(String Key, long[] Val) {
		((DataMap)getObject()).putLongArray(Key, Val);
	}
		
	/**
	 * Adds a String data item to the DataMap
	 * 
	 * Key - a key used to reference the data item
	 * Val - the value to set to.
	 */
	public void PutString(String Key, String Val) {
		((DataMap)getObject()).putString(Key, Val);
	}
		
	/**
	 * Adds a String Array data item to the DataMap
	 * 
	 * Key - a key used to reference the data item
	 * Val - the value to set to.
	 */
	public void PutStringArray(String Key, String[] Val) {
		((DataMap)getObject()).putStringArray(Key, Val);
	}
		
	/**
	 * Removes an item from the DataMap with the given Key
	 */
	public void Remove(String Key) {
		((DataMap)getObject()).remove(Key);
	}
		
	/**
	 * Returns the number of Key-Value pairs currently in the DataMap
	 */
	public int Size() {
		return ((DataMap)getObject()).size();
	}
	
	/**
	 * Returns the DataMap as a ByteArray
	 */
	public byte[] toByteArray() {
		return ((DataMap)getObject()).toByteArray();
	}
		
	/**
	 * Returns a string representation of the DataMap
	 */
	public String toString() {
		return ((DataMap)getObject()).toString();
	}
}
