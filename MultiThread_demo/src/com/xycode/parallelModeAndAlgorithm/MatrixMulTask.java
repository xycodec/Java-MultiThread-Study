package com.xycode.parallelModeAndAlgorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

import org.jmatrices.dbl.Matrix;
import org.jmatrices.dbl.MatrixFactory;
import org.jmatrices.dbl.operator.MatrixOperator;

public class MatrixMulTask extends RecursiveTask<Matrix>{//ForkAndJoin框架,RecursiveTask<V>带有返回值的
	Matrix m1,m2;
	String pos;
	static final int Mul_granularity=8;
	public MatrixMulTask(Matrix m1, Matrix m2, String pos) {
		super();
		this.m1 = m1;
		this.m2 = m2;
		this.pos = pos;
	}

	@Override
	protected Matrix compute() {
		//不断地将大的矩阵分解为小的矩阵相乘,并为每个小矩阵相乘fork一个线程来计算
		if(m1.rows()<=Mul_granularity||m2.cols()<=Mul_granularity) {
			return MatrixOperator.multiply(m1, m2);
		}else {
			//行数rows,列数cols
			Matrix m11=m1.getSubMatrix(1, 1, m1.rows()/2, m1.cols());//矩阵m1的上半部分
			Matrix m12=m1.getSubMatrix(m1.rows()/2+1, 1, m1.rows(), m1.cols());//矩阵m1的下半部分
			
			Matrix m21=m2.getSubMatrix(1, 1, m2.rows(), m2.cols()/2);//矩阵m2的左半部分
			Matrix m22=m2.getSubMatrix(1, m2.cols()/2+1, m2.rows(), m2.cols());//矩阵m2的左半部分
			
			ArrayList<MatrixMulTask> subTasks=new ArrayList<>();
			subTasks.add(new MatrixMulTask(m11, m21, "m1"));
			subTasks.add(new MatrixMulTask(m11, m22, "m2"));
			subTasks.add(new MatrixMulTask(m12, m21, "m3"));
			subTasks.add(new MatrixMulTask(m12, m22, "m4"));
			
			for(MatrixMulTask t:subTasks) {
				t.fork();//fork一个线程来计算
			}
			
			Map<String,Matrix> matrix_result=new HashMap<>();//String原生支持Hash与equals,不用重载了
			for(MatrixMulTask t:subTasks) {
				matrix_result.put(t.pos, t.join());//join返回就算结果,根据pos来确定是那块计算结果
			}
			
			Matrix tmp1=MatrixOperator.horizontalConcatenation(matrix_result.get("m1"), matrix_result.get("m2"));//水平方向合并矩阵
			Matrix tmp2=MatrixOperator.horizontalConcatenation(matrix_result.get("m3"), matrix_result.get("m4"));
			
			Matrix result=MatrixOperator.verticalConcatenation(tmp1, tmp2);//垂直方向合并矩阵,得到真正的结果
			return result;
		}
	}
	
	public static void main(String[] args) {
		ForkJoinPool pool=new ForkJoinPool();
		Matrix m1=MatrixFactory.getRandomMatrix(512, 256, null);
		Matrix m2=MatrixFactory.getRandomMatrix(256, 512, null);
		MatrixMulTask task=new MatrixMulTask(m1,m2,null);
		ForkJoinTask<Matrix> result=pool.submit(task);//future模式
		try {
			System.out.println(result.get());
		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}
		
	}

}
