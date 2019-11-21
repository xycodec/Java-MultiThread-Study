package com.xycode.parallelModeAndAlgorithm;
/**
 * Future模式的实现
 * @author xycode
 *
 */
public class FutureDemo {
	static interface Data{
		public String getResult();
	}
	static class RealData implements Data{
		String result;		
		public RealData(String para,int count) {//真实数据,生成的速度较慢
			if(count<0) {
				throw new NegativeArraySizeException(String.valueOf(count));
			}
			StringBuffer sb=new StringBuffer();
			for(int i=0;i<count;++i) {
				sb.append(para);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			result=sb.toString();
		}
		@Override
		public String getResult() {
			return result;
		}
		
	}
	static class FutureData implements Data{
		RealData realData=null;
		boolean isReady=false;
		public synchronized void setRealData(RealData realData) {//等价于synchronized(this){}
			if(isReady) return; //已经准备好了数据,就不用再计算了,实际上这是一种闭锁,也是线程安全的
			this.realData=realData;
			isReady=true;
			this.notifyAll();
//			synchronized(this) {
//				this.notifyAll();
//			}
			
		}
		@Override
		public String getResult() {
			while(!isReady) {
				try {
					synchronized(this) {
						this.wait();//this表明只作用于当前调用的单个实例
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			return realData.result;
		}
		
	}
	
	static class Client{
		FutureData future=new FutureData();
		public Data request(final String queryStr,final int count) {
			new Thread() {
				@Override
				public void run() {
					try {
						RealData realData=new RealData(queryStr,count);//真正的计算过程
						future.setRealData(realData);//计算好了就将结果设置到FutureData	
					}catch (Exception e) {
						System.err.println("illegal negative size!");
						e.printStackTrace();
					}
			
				}
			}.start();
			return future;//这里返回的并不是真实的数据,而是一个凭证
		}
	}
	
	public static void main(String[] args) {
		Client client1=new Client();
		Client client2=new Client();

		Data data1=client1.request("xycode ",10);//这里是立即返回一个Future凭证
		Data data2=client2.request("xycodec ",20);//这里是立即返回一个Future凭证

		System.out.println("Request Finished!");
		//这里可以处理其它业务逻辑
		System.out.println("Handle Others...");
		try {
			Thread.sleep(200);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("Handle Others Finished!");
		System.out.println("RealData = "+data1.getResult());//这里最好确保真实数据已经生成完毕,否则就会一直在这儿等待,知道真实数据生成完毕
		System.out.println("RealData = "+data2.getResult());//能看出,这里的数据是并行产生的
	}

}
