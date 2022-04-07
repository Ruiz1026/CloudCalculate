package OSE2;

public class CPU {
    protected static int pc;//ָʾָ����߼���ַ
    protected static int ir;//ָʾָ������
    protected static int psw;//ָʾ����״̬
    protected static int Address;//ָʾ��ǰָ����߼���ַ
    private static int cpuTime=0;//����ʱ��
    private static boolean ifCpuWork=false;//����״̬
    protected static boolean ifCpuCloseInterrupt=false;//���ж�λ
    static Process workingProcess=null;//ռ��CPU�Ľ���
    private static boolean ifOne=false;//����ģ��ָ��1
    /*
    ÿִ��һ��CPUʱ���1
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
    ��ϵͳ���û�̬תΪ����̬
     */
    public static void switchUser2Kernel(){
        ifCpuCloseInterrupt=true;
        OSGUI.textField.setText("����̬");
    }
    /*
   ��ϵͳ�Ӻ���̬תΪ�û�̬
     */
    public static void switchKernel2User(){
        ifCpuCloseInterrupt=false;
        OSGUI.textField.setText("�û�̬");
    }
    /*
    ÿ�붼��Ҫ��һ��
     */
    public static void doInstruction(){
        workingProcess.plusRunTime();
        workingProcess.subTimeSlice();
        workingProcess.setPc(CPU.getPc());
        workingProcess.setNewIR();//����PC����ȫ�µ�IR
        ir=workingProcess.getIr();
        workingProcess.setNewAddress();//����PC�����µ��߼���ַ
        Address=workingProcess.getAddress();
        PageManage.CheckPageGraph(Address,workingProcess.Process_PG,workingProcess.OutProcess_PG);//���Ҷ�Ӧ�������ַ
        showMemory(workingProcess.Process_PG);
        if (!workingProcess.isTimeLeft())//���ʱ��Ƭ�����ˣ����ʱ��Ƭ�����жϱ�־��Ҫ��Ϊ��Ч
        	workingProcess.settimeSliceUsed(true);
        /*
         * ����
         */
        OSGUI.textField_1.setText(Integer.toString(workingProcess.getPc()+2));
        OSGUI.textField_2.setText(Integer.toString(workingProcess.getIr()));
        OSGUI.textField_3.setText(Integer.toString(workingProcess.getID()));
        OSGUI.textField_4.setText(Integer.toString((workingProcess.getTimeSlice()+1)));
        OSGUI.textArea_2.append(CPU.getTime()+":[���н���"+workingProcess.getID()+" ָ��"+(workingProcess.getPc()+1)+" ָ������"+workingProcess.getIr()+" ]\n");
        //workingProcess.ShowPhysicPages();
        if(ir==0){//���ݲ�ͬ��ָ��ִ�в�ͬ�Ĳ�����
            CPU.setIfCpuWork(true);
            OSGUI.textField.setText("�û�̬");
            workingProcess.plusPCandWhetherEnd();
        }
        else if(ir==1){
        	if(!ifOne) {
        	 CPU.setIfCpuWork(true);
             OSGUI.textField.setText("�û�̬");
             ifOne=true;
        	}
        	else {
        		CPU.setIfCpuWork(true);
                OSGUI.textField.setText("�û�̬");
                ifOne=false;
                workingProcess.plusPCandWhetherEnd();
        	}
        }
        else if(ir==2){
            switchUser2Kernel();//ϵͳ���ã�����̬
            if(KeyBoard.getKeyBoardState()&&workingProcess.psw!=3)
                workingProcess.blockProcess();
            else 
                KeyBoard.setKeyBoardWork(workingProcess);
            switchKernel2User();//ִ����ɷ����û�̬
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

//���¾�Ϊ����CPU���Եķ���
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
