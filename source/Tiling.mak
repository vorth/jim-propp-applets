# Microsoft Developer Studio Generated NMAKE File, Format Version 4.20
# ** DO NOT EDIT **

# TARGTYPE "Java Virtual Machine Java Workspace" 0x0809

!IF "$(CFG)" == ""
CFG=Tiling - Java Virtual Machine Debug
!MESSAGE No configuration specified.  Defaulting to Tiling - Java Virtual\
 Machine Debug.
!ENDIF 

!IF "$(CFG)" != "Tiling - Java Virtual Machine Release" && "$(CFG)" !=\
 "Tiling - Java Virtual Machine Debug"
!MESSAGE Invalid configuration "$(CFG)" specified.
!MESSAGE You can specify a configuration when running NMAKE on this makefile
!MESSAGE by defining the macro CFG on the command line.  For example:
!MESSAGE 
!MESSAGE NMAKE /f "Tiling.mak" CFG="Tiling - Java Virtual Machine Debug"
!MESSAGE 
!MESSAGE Possible choices for configuration are:
!MESSAGE 
!MESSAGE "Tiling - Java Virtual Machine Release" (based on\
 "Java Virtual Machine Java Workspace")
!MESSAGE "Tiling - Java Virtual Machine Debug" (based on\
 "Java Virtual Machine Java Workspace")
!MESSAGE 
!ERROR An invalid configuration is specified.
!ENDIF 

!IF "$(OS)" == "Windows_NT"
NULL=
!ELSE 
NULL=nul
!ENDIF 
################################################################################
# Begin Project
# PROP Target_Last_Scanned "Tiling - Java Virtual Machine Debug"
JAVA=jvc.exe

!IF  "$(CFG)" == "Tiling - Java Virtual Machine Release"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 0
# PROP BASE Output_Dir ""
# PROP BASE Intermediate_Dir ""
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 0
# PROP Output_Dir ""
# PROP Intermediate_Dir ""
# PROP Target_Dir ""
OUTDIR=.
INTDIR=.

ALL : "$(OUTDIR)\randomHotBits.class" "$(OUTDIR)\Face.class"\
 "$(OUTDIR)\Edge.class" "$(OUTDIR)\randomX.class" "$(OUTDIR)\Tile.class"\
 "$(OUTDIR)\Tiling.class" "$(OUTDIR)\Vertex.class" "$(OUTDIR)\Bitbucket.class"\
 "$(OUTDIR)\randomX\randomLEcuyer.class"

CLEAN : 
	-@erase "$(INTDIR)\Bitbucket.class"
	-@erase "$(INTDIR)\Edge.class"
	-@erase "$(INTDIR)\Face.class"
	-@erase "$(INTDIR)\randomHotBits.class"
	-@erase "$(INTDIR)\randomX.class"
	-@erase "$(INTDIR)\randomX\randomLEcuyer.class"
	-@erase "$(INTDIR)\Tile.class"
	-@erase "$(INTDIR)\Tiling.class"
	-@erase "$(INTDIR)\Vertex.class"

# ADD BASE JAVA /O
# ADD JAVA /O

!ELSEIF  "$(CFG)" == "Tiling - Java Virtual Machine Debug"

# PROP BASE Use_MFC 0
# PROP BASE Use_Debug_Libraries 1
# PROP BASE Output_Dir ""
# PROP BASE Intermediate_Dir ""
# PROP BASE Target_Dir ""
# PROP Use_MFC 0
# PROP Use_Debug_Libraries 1
# PROP Output_Dir ""
# PROP Intermediate_Dir ""
# PROP Target_Dir ""
OUTDIR=.
INTDIR=.

ALL : "$(OUTDIR)\randomHotBits.class" "$(OUTDIR)\Face.class"\
 "$(OUTDIR)\Edge.class" "$(OUTDIR)\randomX.class" "$(OUTDIR)\Tile.class"\
 "$(OUTDIR)\Tiling.class" "$(OUTDIR)\Vertex.class" "$(OUTDIR)\Bitbucket.class"\
 "$(OUTDIR)\randomLEcuyer.class"

CLEAN : 
	-@erase "$(INTDIR)\Bitbucket.class"
	-@erase "$(INTDIR)\Edge.class"
	-@erase "$(INTDIR)\Face.class"
	-@erase "$(INTDIR)\randomHotBits.class"
	-@erase "$(INTDIR)\randomLEcuyer.class"
	-@erase "$(INTDIR)\randomX.class"
	-@erase "$(INTDIR)\Tile.class"
	-@erase "$(INTDIR)\Tiling.class"
	-@erase "$(INTDIR)\Vertex.class"

# ADD BASE JAVA /g
# ADD JAVA /g

!ENDIF 

################################################################################
# Begin Target

# Name "Tiling - Java Virtual Machine Release"
# Name "Tiling - Java Virtual Machine Debug"

!IF  "$(CFG)" == "Tiling - Java Virtual Machine Release"

!ELSEIF  "$(CFG)" == "Tiling - Java Virtual Machine Debug"

!ENDIF 

################################################################################
# Begin Source File

SOURCE=.\randomHotBits.java

!IF  "$(CFG)" == "Tiling - Java Virtual Machine Release"


"$(INTDIR)\randomHotBits.class" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "Tiling - Java Virtual Machine Debug"


"$(INTDIR)\randomHotBits.class" : $(SOURCE) "$(INTDIR)"


!ENDIF 

# End Source File
################################################################################
# Begin Source File

SOURCE=.\Face.java

!IF  "$(CFG)" == "Tiling - Java Virtual Machine Release"


"$(INTDIR)\Face.class" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "Tiling - Java Virtual Machine Debug"


"$(INTDIR)\Face.class" : $(SOURCE) "$(INTDIR)"


!ENDIF 

# End Source File
################################################################################
# Begin Source File

SOURCE=.\Edge.java

!IF  "$(CFG)" == "Tiling - Java Virtual Machine Release"


"$(INTDIR)\Edge.class" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "Tiling - Java Virtual Machine Debug"


"$(INTDIR)\Edge.class" : $(SOURCE) "$(INTDIR)"


!ENDIF 

# End Source File
################################################################################
# Begin Source File

SOURCE=.\randomX.java

!IF  "$(CFG)" == "Tiling - Java Virtual Machine Release"


"$(INTDIR)\randomX.class" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "Tiling - Java Virtual Machine Debug"


"$(INTDIR)\randomX.class" : $(SOURCE) "$(INTDIR)"


!ENDIF 

# End Source File
################################################################################
# Begin Source File

SOURCE=.\Tile.java

!IF  "$(CFG)" == "Tiling - Java Virtual Machine Release"


"$(INTDIR)\Tile.class" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "Tiling - Java Virtual Machine Debug"


"$(INTDIR)\Tile.class" : $(SOURCE) "$(INTDIR)"


!ENDIF 

# End Source File
################################################################################
# Begin Source File

SOURCE=.\Tiling.java

!IF  "$(CFG)" == "Tiling - Java Virtual Machine Release"


"$(INTDIR)\Tiling.class" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "Tiling - Java Virtual Machine Debug"


"$(INTDIR)\Tiling.class" : $(SOURCE) "$(INTDIR)"


!ENDIF 

# End Source File
################################################################################
# Begin Source File

SOURCE=.\Vertex.java

!IF  "$(CFG)" == "Tiling - Java Virtual Machine Release"


"$(INTDIR)\Vertex.class" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "Tiling - Java Virtual Machine Debug"


"$(INTDIR)\Vertex.class" : $(SOURCE) "$(INTDIR)"


!ENDIF 

# End Source File
################################################################################
# Begin Source File

SOURCE=.\Tiling.html

!IF  "$(CFG)" == "Tiling - Java Virtual Machine Release"

!ELSEIF  "$(CFG)" == "Tiling - Java Virtual Machine Debug"

!ENDIF 

# End Source File
################################################################################
# Begin Source File

SOURCE=.\Bitbucket.java

!IF  "$(CFG)" == "Tiling - Java Virtual Machine Release"


"$(INTDIR)\Bitbucket.class" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "Tiling - Java Virtual Machine Debug"


"$(INTDIR)\Bitbucket.class" : $(SOURCE) "$(INTDIR)"


!ENDIF 

# End Source File
################################################################################
# Begin Source File

SOURCE=.\randomLEcuyer.java

!IF  "$(CFG)" == "Tiling - Java Virtual Machine Release"


"$(INTDIR)\randomX\randomLEcuyer.class" : $(SOURCE) "$(INTDIR)"


!ELSEIF  "$(CFG)" == "Tiling - Java Virtual Machine Debug"


"$(INTDIR)\randomLEcuyer.class" : $(SOURCE) "$(INTDIR)"


!ENDIF 

# End Source File
# End Target
# End Project
################################################################################
