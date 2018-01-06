/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package org.usfirst.frc.team2137.robot;

import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the build.properties file in the
 * project.
 */
public class Robot extends IterativeRobot {

	// Scott McBride Started Here with Robot Definition.
	Victor leftFrontMotor = new Victor(0);
	Victor leftRearMotor = new Victor(2);
	Victor rightFrontMotor = new Victor(1);
	Victor rightRearMotor = new Victor(3);
	Compressor airPump = new Compressor(0);
	DoubleSolenoid highGear = new DoubleSolenoid(0, 1);
	Encoder leftDistance = new Encoder(0, 1, false, Encoder.EncodingType.k4X);
	// leftDistance.setDistancePerPulse(1);

	// leftDistance.setDistnacePerPulse(0.05026548245743669181540229413247);

	Encoder rightDistance = new Encoder(2, 3, false, Encoder.EncodingType.k4X);

	// Scott McBride Operator Interface Setups
	Joystick driverStick = new Joystick(0);
	int throttleAxis = 1;
	int wheelAxis = 4;
	int highGearButton = 4;

	// Scott McBride Stored Constants
	double axisDeadband = 0.15;
	double quickTurnConstant = 0.3;
	double quickTurnSensitivity = 0.7;
	double speedTurnSensitivity = 0.7;
	double leftMotorValue = 0.0;
	double rightMotorValue = 0.0;

	boolean highGearEnable = false;
	boolean highGearLast = false;
	boolean gearToggle = false;

	// Scott McBride Ends.

	private static final String kDefaultAuto = "Default";
	private static final String kCustomAuto = "My Auto";
	private String m_autoSelected;
	private SendableChooser<String> m_chooser = new SendableChooser<>();

	/**
	 * This function is run when the robot is first started up and should be used
	 * for any initialization code.
	 */
	@Override
	public void robotInit() {
		m_chooser.addDefault("Default Auto", kDefaultAuto);
		m_chooser.addObject("My Auto", kCustomAuto);
		SmartDashboard.putData("Auto choices", m_chooser);
	}

	/**
	 * This autonomous (along with the chooser code above) shows how to select
	 * between different autonomous modes using the dashboard. The sendable chooser
	 * code works with the Java SmartDashboard. If you prefer the LabVIEW Dashboard,
	 * remove all of the chooser code and uncomment the getString line to get the
	 * auto name from the text box below the Gyro
	 *
	 * <p>
	 * You can add additional auto modes by adding additional comparisons to the
	 * switch structure below with additional strings. If using the SendableChooser
	 * make sure to add them to the chooser code above as well.
	 */
	@Override
	public void autonomousInit() {
		m_autoSelected = m_chooser.getSelected();
		// autoSelected = SmartDashboard.getString("Auto Selector",
		// defaultAuto);
		System.out.println("Auto selected: " + m_autoSelected);
	}

	/**
	 * This function is called periodically during autonomous.
	 */
	@Override
	public void autonomousPeriodic() {
		switch (m_autoSelected) {
		case kCustomAuto:
			// Put custom auto code here
			break;
		case kDefaultAuto:
		default:
			// Put default auto code here
			break;
		}
	}

	/**
	 * This function is called periodically during operator control.
	 */
	@Override
	public void teleopPeriodic() {
		// Negate the raw value, as all the way up on Y is -1.
		double driverThrottle = -driverStick.getRawAxis(throttleAxis);
		double driverWheel = driverStick.getRawAxis(wheelAxis);
		driverThrottle = Deadband(driverThrottle, axisDeadband);
		driverWheel = Deadband(driverWheel, axisDeadband);

		// Halo Driver Control Algorithm
		if (Math.abs(driverThrottle) < quickTurnConstant) {
			rightMotorValue = driverThrottle - driverWheel * quickTurnSensitivity;
			leftMotorValue = driverThrottle + driverWheel * quickTurnSensitivity;
		} else {
			rightMotorValue = driverThrottle - Math.abs(driverThrottle) * driverWheel * speedTurnSensitivity;
			leftMotorValue = driverThrottle + Math.abs(driverThrottle) * driverWheel * speedTurnSensitivity;
		}
		rightMotorValue = Limit(rightMotorValue);
		leftMotorValue = Limit(leftMotorValue);

		// Low Gear/High Gear toggle and solenoid
		// highGearLast = highGearEnable;
		highGearEnable = driverStick.getRawButtonPressed(highGearButton);

		if (highGearEnable = true)
			gearToggle = !gearToggle;

		if (gearToggle = true) {
			highGear.set(DoubleSolenoid.Value.kForward);
		} else {
			highGear.set(DoubleSolenoid.Value.kReverse);
		}
		// Write Motor Values
		leftFrontMotor.set(leftMotorValue);
		leftRearMotor.set(leftMotorValue);
		rightFrontMotor.set(rightMotorValue);
		rightRearMotor.set(rightMotorValue);

		// Added variables to show encoder data, need to work out scales.
		double leftInches = leftDistance.getDistance();
		double rightInches = rightDistance.getDistance();

	}

	/**
	 * This function is called periodically during test mode.
	 */
	@Override
	public void testPeriodic() {
	}

	double Deadband(double x, double y) {
		if (Math.abs(x) < y)
			x = 0;
		return x;
	}

	double Limit(double x) {
		return Math.max(-1.0, Math.min(x, 1.0));
	}

}
