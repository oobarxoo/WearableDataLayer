package barxdroid.wearabledatalayer;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;

import com.google.android.gms.wearable.Asset;
import android.graphics.Bitmap;
import android.net.Uri;
import anywheresoftware.b4a.BA.Author;
import anywheresoftware.b4a.BA.ShortName;

@ShortName("WearableAsset")
@Author("BarxDroid")

public class WearableAsset {
	
	/**
	 * This method doesn't do anything, it is here purely for informational purposes.
	 * An asset is used to send a binary blob of data such as an image.
	 * You attach an asset to a DataItem.
	 * The system takes care of conserving bluetooth by caching large assets to avoid re-transmission.
	 */
	public void Info() {
		
	}
	
	/**
	 * Creates an Asset to use from a Bitmap
	 */
	public Asset CreateFromBitmap(Bitmap bitmap) {
		final ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream);
		return Asset.createFromBytes(byteStream.toByteArray());
	}
	


	
	/**
	 * Creates an Asset to use from a File  
	 */
	public Asset CreateFromFile(String Dir, String Filename) throws FileNotFoundException {
		File mFile = new File(Dir, Filename);
		//ParcelFileDescriptor fd = new ParcelFileDescriptor(ParcelFileDescriptor.open(mFile, ParcelFileDescriptor.MODE_READ_WRITE));
		return Asset.createFromUri(Uri.fromFile(mFile));
	}
}
