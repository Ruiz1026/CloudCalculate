package OSE2;

public class CPU {
    protected static int pc;//指示指令的逻辑地址
    protected static int ir;//指示指令类型
    protected static int psw;//指示进程状态
    protected static int Address;//指示当前指令的逻辑地址
    private static int cpuTime=0;//运行时间
    private static boolean ifCpuWork=false;//工作状态
    protected static boolean ifCpuCloseInterrupt=false;//关中断位
    static Process workingProcess=null;//占有CPU的进程
    private static boolean ifOne=false;//用来模拟指令1
    /*
    每执行一次CPU时间加1
     */
    public static void timeAddAdd()
    {
        cpuTime++;
    }
    public static int getTime()
    {
        return cpuTime;
    }
    /*
    把系统从用户态转为核心态
     */
    public static void switchUser2Kernel(){
        ifCpuCloseInterrupt=true;
        OSGUI.textField.setText("核心态");
    }
    /*
   把系统从核心态转为用户态
     */
    public static void switchKernel2User(){
        ifCpuCloseInterrupt=false;
        OSGUI.textField.setText("用户态");
    }
    /*
    每秒都需要做一次
     */
    public static void doInstruction(){
        workingProcess.plusRunTime();
        workingProcess.subTimeSlice();
        workingProcess.setPc(CPU.getPc());
        workingProcess.setNewIR();//根据PC设置全新的IR
        ir=workingProcess.getIr();
        workingProcess.setNewAddress();//根据PC设置新的逻辑地址
        Address=workingProcess.getAddress();
        PageManage.CheckPageGraph(Address,workingProcess.Process_PG,workingProcess.OutProcess_PG);//查找对应的物理地址
        showMemory(workingProcess.Process_PG);
        if (!workingProcess.isTimeLeft())//如果时间片用完了，这个时间片用完判断标志需要变为有效
        	workingProcess.settimeSliceUsed(true);
        /*
         * 更新
         */
        OSGUI.textField_1.setText(Integer.toString(workingProcess.getPc()+2));
        OSGUI.textField_2.setText(Integer.toString(workingProcess.getIr()));
        OSGUI.textField_3.setText(Integer.toString(workingProcess.getID()));
        OSGUI.textField_4.setText(Integer.toString((workingProcess.getTimeSlice()+1)));
        OSGUI.textArea_2.append(CPU.getTime()+":[运行进程"+workingProcess.getID()+" 指令"+(workingProcess.getPc()+1)+" 指令类型"+workingProcess.getIr()+" ]\n");
        //workingProcess.ShowPhysicPages();
        if(ir==0){//根据不同的指令执行不同的操作。
            CPU.setIfCpuWork(true);
            OSGUI.textField.setText("用户态");
            workingProcess.plusPCandWhetherEnd();
        }
        else if(ir==1){
        	if(!ifOne) {
        	 CPU.setIfCpuWork(true);
             OSGUI.textField.setText("用户态");
             ifOne=true;
        	}
        	else {
        		CPU.setIfCpuWork(true);
                OSGUI.textField.setText("用户态");
                ifOne=false;
                workingProcess.plusPCandWhetherEnd();
        	}
        }
        else if(ir==2){
            switchUser2Kernel();//系统调用，核心态
            if(KeyBoard.getKeyBoardState()&&workingProcess.psw!=3)
                workingProcess.blockProcess();
            else 
                KeyBoard.setKeyBoardWork(workingProcess);
            switchKernel2User();//执行完成返回用户态
            CPU.ifCpuWork=false;
        }
        else if(ir==3){
            switchUser2Kernel();
            if(Display.getDisplayState()&&workingProcess.getPsw()!=3)
                workingProcess.blockProcess();
            else
                Display.setDisplayWork(workingProcess);
            switchKernel2User();
            CPU.ifCpuWork=false;
        }
        else if(ir==4){
            switchUser2Kernel();
            if(ReadDisk.getReadDiskState()&&workingProcess.getPsw()!=3) {
            	ReadDisk.P();
                workingProcess.blockProcess();
            }
            else
            	ReadDisk.setReadDiskWork(workingProcess);
            switchKernel2User();
            CPU.ifCpuWork=false;
        }
        else if(ir==5){
        	switchUser2Kernel();
            if(WriteDisk.getWriteDiskState()&&workingProcess.getPsw()!=3) {
            	WriteDisk.P();
                workingProcess.blockProcess();
            }
            else 
            	WriteDisk.setWriteDiskWork(workingProcess);
            switchKernel2User();
            CPU.ifCpuWork=false;
        }
        else if(ir==6){
        	switchUser2Kernel();
            if(Well.getWellState()&&workingProcess.getPsw()!=3) {
            	Well.P();
                workingProcess.blockProcess();
            }
            else 
            	Well.setWellWork(workingProcess);
            switchKernel2User();
            CPU.ifCpuWork=false;
        }
        PCB.showReadyQueueID();
        PCB.showBlockQueue1ID();
        PCB.showBlockQueue2ID();
        PCB.showBlockQueue3ID();
        PCB.showBlockQueue4ID();
        PCB.showBlockQueue5ID();
        ReadDisk.showSignal();
        Memory.showMemory();
        if(CPU.workingProcess!=null) {
        	Memory.showMemoryInstructions(CPU.workingProcess);
        }
        Disk.ShowDisk();
        //workingProcess.ShowPhysicPages();
    }

//以下均为操作CPU属性的方法
    public static boolean getifCpuWork(){
        return ifCpuWork;
    }
    public static void setIfCpuWork(boolean t){
        ifCpuWork=t;
    }
    public static void setPc(int pc1){
        pc=pc1;
    }
    public static void setIR(int ir1){
        ir=ir1;
    }
    public static void setPSW(int psw1){
        psw=psw1;
    }
    public static void setIfCpuCloseInterrupt(boolean temp){
        ifCpuCloseInterrupt=temp;
    }
    public static int getPc(){
        return pc;
    }

    public static int getIr() {
        return ir;
    }
    public static void showMemory(PageGraph[] PG) {
    	String s="";
    	for(PageGraph e:PG) {
    		 s+=String.valueOf(e.Logic)+"\n\r";
    	}
    	OSGUI.textField_7.setText(s);
    }
}
