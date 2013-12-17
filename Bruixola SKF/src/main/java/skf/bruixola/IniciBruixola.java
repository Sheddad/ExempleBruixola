package skf.bruixola;

import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class IniciBruixola extends ActionBarActivity implements SensorEventListener {

    // SKF. Inicialitzem imatge i Textos
    private ImageView imatge;

    TextView txtB;
    TextView txtBx;
    TextView txtBy;
    TextView txtBz;

    // SKF. Posem els graus a O
    private float currentDegree = 0f;

    // SKF. Definim els sensors
    private SensorManager mSensorManager;
    private Sensor mRotation;
    private Sensor mMagneticField;
    private Sensor mMagneticFieldUncalibrated;

    // SKF. Sensor de Brúixola
    boolean hasCompass; // Per veure si tenim Brúixola (Rotació)
    boolean hasMagneticField; // Per veure si tenim Sensor de Camp Magnètic


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inici_bruixola);

        imatge = (ImageView) findViewById(R.id.imBruixolaPunter);

        //Inicialitzem els TextsViews
        txtB = (TextView) findViewById(R.id.txtB);
        txtBx = (TextView) findViewById(R.id.txtBx);
        txtBy = (TextView) findViewById(R.id.txtBy);
        txtBz = (TextView) findViewById(R.id.txtBz);

        /*
         * SKF. Inicialitzem el SensorManager
		 */
        PackageManager manager = getPackageManager();
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

         /*
         * SKF. Comprovem que tenim el Sensor de ROTACIÓ en el dipositiu i
		 * declarem el Sensor
		 */
        hasCompass = manager
                .hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS);
        if (hasCompass) {
            mRotation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        }
        /*
         * SKF. Comprovem que tenim el Sensor de Camp Magnètic en el dipositiu i
		 * declarem el Sensor
		 */
        hasMagneticField = manager
                .hasSystemFeature(PackageManager.FEATURE_SENSOR_COMPASS);
        if (hasMagneticField) {
            mMagneticField = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        //SKF. Inicialitzem els sensors si els tenim

        if (hasCompass) {
            mSensorManager.registerListener(this, mRotation,
                    SensorManager.SENSOR_DELAY_GAME);
        }

        if (hasMagneticField) {
            mSensorManager.registerListener(this, mMagneticField,
                    SensorManager.SENSOR_DELAY_NORMAL);
        }

    }

    public void onClickCompass(View v) {

        RelativeLayout rlBruixola = (RelativeLayout) findViewById(R.id.rlBruixola);

        if (rlBruixola.getVisibility() == View.INVISIBLE) {
            rlBruixola.setVisibility(View.VISIBLE);
        } else {
            rlBruixola.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        // SKF. Apaguem els listeners per estalviar energia
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        switch (event.sensor.getType()) {
            case Sensor.TYPE_ORIENTATION:
                // get the angle around the z-axis rotated
                float degree = Math.round(event.values[0]);

                // create a rotation animation (reverse turn degree degrees)
                RotateAnimation ra = new RotateAnimation(
                        currentDegree,
                        -degree,
                        Animation.RELATIVE_TO_SELF, 0.5f,
                        Animation.RELATIVE_TO_SELF,
                        0.5f);

                // how long the animation will take place
                ra.setDuration(210);

                // set the animation after the end of the reservation status
                ra.setFillAfter(true);

                // Start the animation
                imatge.startAnimation(ra);
                currentDegree = -degree;

                break;

            case Sensor.TYPE_MAGNETIC_FIELD:
                txtBx.setText("Bx =" + String.format("%.1f", event.values[0]) + " μT");
                txtBy.setText("By =" + String.format("%.1f", event.values[1]) + " μT");
                txtBz.setText("Bz =" + String.format("%.1f", event.values[2]) + " μT");
                break;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // not in use
    }

}