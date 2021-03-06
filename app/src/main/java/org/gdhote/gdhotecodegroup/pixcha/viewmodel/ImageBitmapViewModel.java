package org.gdhote.gdhotecodegroup.pixcha.viewmodel;

import android.graphics.Bitmap;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ImageBitmapViewModel extends ViewModel {

    private MutableLiveData<Bitmap> originalBitmap;
    private Bitmap bitmap;
    private MutableLiveData<Bitmap> croppedBitmap;
    private MutableLiveData<Bitmap> filteredBitmap;

    public void initialize() {
        originalBitmap = new MutableLiveData<>();
        croppedBitmap = new MutableLiveData<>();
        filteredBitmap = new MutableLiveData<>();
        originalBitmap.setValue(null);
        croppedBitmap.setValue(null);
        filteredBitmap.setValue(null);

        bitmap = null;
    }

    public void setOriginalBitmap(Bitmap bitmap) {
        this.originalBitmap.setValue(bitmap);
    }

    private void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setCroppedBitmap(Bitmap bitmap) {
        this.croppedBitmap.setValue(bitmap);
    }

    public void setFilteredBitmap(Bitmap bitmap) {
        this.filteredBitmap.setValue(bitmap);
    }

    public MutableLiveData<Bitmap> getOriginalBitmap() {
        return originalBitmap;
    }

    public Bitmap getBitmap() {
        return originalBitmap.getValue();
    }

    public MutableLiveData<Bitmap> getCroppedBitmap() {
        return croppedBitmap;
    }

    public MutableLiveData<Bitmap> getFilteredBitmap() {
        return filteredBitmap;
    }
}
