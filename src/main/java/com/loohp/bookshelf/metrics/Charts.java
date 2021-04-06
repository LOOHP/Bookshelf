package com.loohp.bookshelf.metrics;

import java.util.concurrent.Callable;

import com.loohp.bookshelf.Bookshelf;
import com.loohp.bookshelf.BookshelfManager;

public class Charts {	
	
	public static void loadCharts(Metrics metrics) {
		metrics.addCustomChart(new Metrics.SingleLineChart("total_bookshelves", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                return BookshelfManager.getJsonObject().size();
            }
        }));
	    
	    metrics.addCustomChart(new Metrics.SimplePie("hoppers_enabled", new Callable<String>() {
	        @Override
	        public String call() throws Exception {
	        	String string = "Disabled";
	        	if (Bookshelf.enableHopperSupport == true) {
	        		string = "Enabled";
	        	}
	            return string;
	        }
	    }));
	    
	    metrics.addCustomChart(new Metrics.SimplePie("droppers_enabled", new Callable<String>() {
	        @Override
	        public String call() throws Exception {
	        	String string = "Disabled";
	        	if (Bookshelf.enableDropperSupport == true) {
	        		string = "Enabled";
	        	}
	            return string;
	        }
	    }));
	    
	    metrics.addCustomChart(new Metrics.SimplePie("enchtable_enabled", new Callable<String>() {
	        @Override
	        public String call() throws Exception {
	        	String string = "Disabled";
	        	if (Bookshelf.enchantmentTable == true) {
	        		string = "Enabled";
	        	}
	            return string;
	        }
	    }));
	    
	    metrics.addCustomChart(new Metrics.SingleLineChart("average_hopper_process_time", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
            	int num = Integer.MAX_VALUE;
            	if (Bookshelf.lastHopperTime < 2147483647) {
            		num = (int) Bookshelf.lastHopperTime;
            	}
                return num;
            }
        }));
	    
	    metrics.addCustomChart(new Metrics.SingleLineChart("average_hopper_minecart_process_time", new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
            	int num = Integer.MAX_VALUE;
            	if (Bookshelf.lastHoppercartTime < 2147483647) {
            		num = (int) Bookshelf.lastHoppercartTime;
            	}
                return num;
            }
        }));
	}

}
