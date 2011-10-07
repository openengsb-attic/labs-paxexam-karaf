package org.openengsb.labs.paxexam.karaf.options;

import org.ops4j.pax.exam.Option;

/**
 * The karaf pax-logging configuration is typically not a file manipulated very often. Therefore we take the freedom of
 * adding a console logger and changing the log level directly. IF you like to configure the file manually (or had so in
 * your distribution) add this option to avoid any automatic modifications to this file!
 */
public class DoNotModifyLogOption implements Option {

}
