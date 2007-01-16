package documents;

/*
 * To-do LIST:
 * 
 * (OPEN) - under discussion
 * (DEF)  - deferred by some other bug/feature
 * (Ryan | Drew) - assigned
 * (WONTFIX) - 
 * 
 * 
 *
 * ==========[ NOMINATED TO CLOSE ]==========
 *
 * 
 * 
 * 
 * ==========[ GENERAL ISSUES ]==========
 * 
 * TODO: (Ryan) Automate jEAC build scripts
 * TODO: (Ryan) Icon for win32 build, better appicon
 * 
 * ----------[ Bugs outstanding ]----------
 * 
 * 
 * 
 * 
 * ==========[ DEFERRED ]==========
 * 
 * TODO: (DEF) Add save warning when overwriting existing .eac files
 * TODO: (DEF) Save last accessed file locatoin
 * FIXME: (DEF) LLA listbox renders improperly on OSX
 * 			- try GBC on LLA listbox? ... NOPE -RV
 * FIXME: (DEF) Driver menu is not cleared when EAC cannot connect (network error)
 * FIXME: (DEF) Driver menu is not properly set on successful configuration load
 * TODO: (DEF) Abstract strings used in load/save menu and the FileListener
 * TODO: (DEF) USBDriver - handle LLA_SRC/LLA_SNK states
 * TODO: (DEF) USBDriver - allow for dual-mode source/sink+LLA nodes
 * TODO: (DEF) USBDriver - look for NOK for boolean return values
 * 			- the good thing about the uEAC is that the error checking is onboard --
 * 			  writeSentence should be modified to return true/false, and that should be 
 * 			  propogated up the call stack -RV
 * TODO: (DEF) Change axis coloring, 
 * TODO: (DEF) remove X-Y-Z labels
 * 
 * 
 * 
 * ==========[ CLOSED ]==========
 *
 * xTODO: (Ryan) Implement smart driver loading (probing) for USBDriver
 * 			- pending OSX testing
 * xTODO: (DEF) Keep the HAL conversation about decoupling JEAC from driver
 * xTODO: (Drew) re-add OSX menu customization to run object
 * xTODO: (Drew) Take out the deprecated call in EthernetDriver
 * xTODO: (Drew) ConnectionException - generalize driver exceptions
 * 			- generalize exception handling or otherwise mark off exception-related tasks
 * xTODO: (WONTFIX) Wire connections should use ok-cancel dialogs
 * xFIXME: (Drew) EthernetDriver is sending waaay too much current (should be 0.0-0.2)
 * xTODO: (OPEN) about window
 * xTODO: (Ryan) uEAC - lla support
 * xTODO: (AUDIT): Verify there are no off-by-one errors.  UI is 1-based, EAC is generally 0-based.
 * xFIXME: (Ryan) LLA Inspector checkbox does not clear on close
 * xTODO: (Ryan) Reset update thread after closing the LLA inspector
 * xTODO: Load/save does not need to reconfigure all the time
 * xTODO: (WONTFIX) Add custom ControlFrame icons
 * xFIXME: (Open) Visualization not cleared on failed deserialization
 * xFIXME: (Ryan) Radio button does not clear on disconnect
 * xTODO: (Ryan) uEAC - fix reset
 * xTODO: (Ryan) uEAC - fix reload
 * xTODO: (WONTFIX) Implement readCurrent for the uEAC, for slider synchronization
 * xTODO: (WONTFIX) Detect network problems
 * 			- Status bar supports WARNING flag, which indicates communication problems -RV
 * xTODO: (Ryan) Sourceforge project
 * 			- ready to go -RV
 * xTODO: (Drew) CONFIGURATION SAVED STATES
 * 			- Make reconfigurable?
 * xTODO: (OPEN) Component initalization: When adding components, should we initialize their
 *       values to zero, or should we report N/A, since we cannot determine their state?
 *       	- I argue for the latter, because the former is a "destructive" change -RV
 * xTODO: (OPEN) Investigate ways to improve NodeMap readability
 * xTODO: (Ryan) Adjust dimensions of NodeMap buttons
 * xTODO: (Ryan) Manually compensate graph size (for proper aspect ratio)
 * 			- Set by GBC with nodemap constraints?
 * xFIXME: (Ryan) translateSliderValue and sliderMapping... it needs to be fixed
 * 			xTODO: (Ryan) Units - uA vs. mA
 * 					- If you've seen the simulator code Bryce wrote, he uses uA
 * 					- Look at the EAC XOR demo, it's in uA (115, by default) -RV
 * 					- I'm using uA, so there. :-P  -RV
 * 			xTODO: Audit LLA Inspector Frame values
 * xFIXME: (Ryan) 2D key acellerator does not fully remove panel
 * xTODO: (Ryan) Audit graph orientation
 * 			- Proposed: 0, 45, 90 orientation (nodemap, 3D, 2D)
 * 			- Actual: 0, 45, 0
 * xFIXME: (OPEN) It seems that randomly when you open one ControlFrame and then switch/disconnect
 * 			it stays open... but I can't duplicate the problem reliably
 * xTODO: (Ryan) Disable graph autoscaling
 * 			- No, let's keep it on
 * xFIXME: (Ryan) Adding 2D panel crashes on live drivers
 * xFIXME: (Drew) Fix gradient out-of-sync error
 * 			- NOT JMathTools related -RV
 * 			- Maybe fixed? -DK
 * xTODO: (Drew) Implement "ghetto mutex" (driver locking)
 * 			- Maybe fixed? -DK
 * xFIXME: [!] (Ryan) Isolate the cause of the Windows crash
 * 			- It seems to crash most often when working with LLAs -RV
 * 			- Sys-out-printing seems to help (flow control problem?) -RV
 * 			- Maybe fixed? -RV
 * xTODO: (OPEN) [!] Package as a JAR
 * xTODO: (DEF) add mnemonics to driver list
 * 			- Minor issue, not easy to do -RV
 * 			- Added accelators instead -RV
 * xTODO: (DEF) Put the LLA window on a timed thread
 * xTODO: (Ryan) uEAC driver
 * xTODO: (Ryan) consolidate LLA reporting
 * xTODO: (Ryan) Add 2D view
 * xFIXME: (Drew) OSX does not clear panels on disconnect
 * xTODO: (OPEN) Modify the UI to be non-blocking (thinking of freeze ups on network lag)
 * xTODO: (OPEN) UI handler for driver lag (splash screen)
 * 			- Since Swing is single threaded, it blocks waiting for the driver to load;
 * 			  see LoadingFrame for a loading window that would work if Swing didn't suck -RV
 * 
 */

public class TodoList { }
