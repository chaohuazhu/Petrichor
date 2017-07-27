## Petrichor - a face detection mobile application

This project use google's face detection api and cross compiled seeta face detection on android on real-time face detection base on video frames. It's provide a common application structure of `android.hardware.camera` (old api) preview matter and a way to process frame in video. And draw face rectangle detection on a drawable view.

We may improve this project that some native process can be accelecrlate by opencl on gpu. Also 3-D face rebuild may be the next main funtion.


## Efficiency
Function|Time cost(average)
--------|:---------------:
Seeta   |≈1200ms
Google  |≈200ms
