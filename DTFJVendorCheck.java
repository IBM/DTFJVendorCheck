/*
 * Copyright 2025 IBM Corp. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
import java.io.File;
import java.util.Iterator;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.ibm.dtfj.image.Image;
import com.ibm.dtfj.image.ImageAddressSpace;
import com.ibm.dtfj.image.ImageProcess;
import com.ibm.dtfj.image.ImageFactory;
import com.ibm.dtfj.java.JavaRuntime;

/**
 * usage: java DTFJVendorCheck file
 * 
 * Where file is a J9 diagnostic such as a core dump or PHD.
 * 
 * This uses process return codes to analyze the dump:
 * 
 * <ol>
 * <li>1: File not specified</li>
 * <li>2: Some exception caught</li>
 * <li>3: The dump was produced by IBM Java</li>
 * <li>4: The dump was produced by Eclipse OpenJ9 (non-IBM Java) or Java &gt;= 10</li>
 * <li>5: The dump was produced by an unknown vendor</li>
 * <li>6: No DTFJ dump found</li>
 * <li>7: Stored version string is null</li>
 * </ol>
 * 
 * @author kevin.grigorenko@us.ibm.com
 */
public class DTFJVendorCheck {
	private static void usage(String error) {
		System.err.println("Error: " + error);
		System.err.println("usage: java DTFJVendorCheck file");
		System.exit(1);
	}

	public static void main(String[] args) {
		if (args == null || args.length == 0) {
			usage("file missing");
		}
		boolean quiet = false;
		String file = null;
		for (int i = 0; i < args.length; i++) {
			String arg = args[i];
			if ("-q".equals(arg)) {
				quiet = true;
			} else {
				file = arg;
			}
		}
		if (file == null || file.length() == 0) {
			usage("file missing");
		}
		try {
			// https://eclipse.dev/openj9/docs/interface_dtfj/
			Class<?> factoryClass = Class.forName("com.ibm.dtfj.image.j9.ImageFactory");
			ImageFactory factory = (ImageFactory) factoryClass.getDeclaredConstructor().newInstance();
			Image image = factory.getImage(new File(file));
			Iterator<?> adressSpaces = image.getAddressSpaces();
			if (adressSpaces.hasNext()) {
				ImageAddressSpace addressSpace = (ImageAddressSpace) adressSpaces.next();
				Iterator<?> processes = addressSpace.getProcesses();
				if (processes.hasNext()) {
					ImageProcess process = (ImageProcess) processes.next();
					Iterator<?> runtimes = process.getRuntimes();
					if (runtimes.hasNext()) {
						JavaRuntime runtime = (JavaRuntime) runtimes.next();

						// This is not available with PHDs, but getVersion() should be fine
						//String producingVmName = runtime.getSystemProperty("java.vm.name");
						String producingVmName = runtime.getVersion();
						if (!quiet) {
							System.out.println("Dump version: " + producingVmName);
						}

						if (producingVmName == null) {
							if (!quiet) {
								System.err.println("Stored version string is null");
							}
							System.exit(7);
						} else {
	
							Pattern p = Pattern.compile("build [0-9][0-9]");
							Matcher m = p.matcher(producingVmName);
							
							if ("IBM J9 VM".equals(producingVmName)) {
								if (!quiet) {
									System.out.println("Dump produced by IBM Java");
								}
								System.exit(3);
							} else if ("Eclipse OpenJ9 VM".equals(producingVmName) || producingVmName.contains("Semeru Runtime")) {
								// Examples:
								// JRE 1.8.0 AIX ppc64-64 (build 1.8.0_402-b06) IBM Semeru Runtime Open Edition
								if (!quiet) {
									System.out.println("Dump produced by Eclipse OpenJ9 (non-IBM Java)");
								}
								System.exit(4);
							} else if (m.find()) {
								if (!quiet) {
									System.out.println("Dump produced by Java >= 10");
								}
								System.exit(4);
							} else {
								if (!quiet) {
									System.err.println("Dump produced by unknown vendor: " + producingVmName);
								}
								System.exit(5);
							}
						}
					}
				}
			}

			if (!quiet) {
				System.err.println("No DTFJ dump found");
			}
			System.exit(6);
		} catch (Throwable t) {
			if (!quiet) {
				t.printStackTrace();
			}
			System.exit(2);
		}
	}
}
