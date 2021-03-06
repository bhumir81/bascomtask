/************************************************************************
Copyright 2018 eBay Inc.
Author/Developer: Brendan McCarthy
 
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at
 
    https://www.apache.org/licenses/LICENSE-2.0
 
Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
**************************************************************************/
package com.ebay.bascomtask.main;

import java.util.ArrayList;
import java.util.List;

import com.ebay.bascomtask.flexeq.FlexEq;

/**
 * Timing results from profiling.
 * 
 * @author bremccarthy
 */
public class TaskStat {
    
    public List<Graph> graphs = new ArrayList<>();

    public static abstract class Timing {
        public int called = 0;
        
        @FlexEq.LongInRange(40)
        public long aggregateTime;
        
        @FlexEq.LongInRange(40)
        public long minTime = -1; // >=0 only after at least one run
        
        @FlexEq.LongInRange(40)
        public long maxTime;

        public long getAvgTime() {
            return aggregateTime / called;
        }
        
        void update(long duration) {
            called++;
            aggregateTime += duration;
            if (minTime < 0 || duration < minTime) {
                minTime = duration;
            }
            if (duration > maxTime) {
                maxTime = duration;
            }
        }
    }
    
    /**
     * The combined results from operations on orchestrators with the same name.
     */
    static public class Graph {
        public final String name;
        private final List<Path> paths = new ArrayList<>();
        
        Graph(String name) {
            this.name = name;
        }
        
        public List<Path> getPaths() {
            return paths;
        }

        public Path path() {
            Path path = new Path();
            paths.add(path);
            return path;
        }
    }
    
    /**
     * An ordered list of task segments within an orchestrator graph each connected by dependency.
     * Every node in a graph constitutes at least one path, and if it has inputs it will be the
     * endpoint in multiple paths. For example, a diamond would have 5 paths:
     * <ul>
     * <li> Top
     * <li> Top-Left
     * <li> Top-Right
     * <li> Top-Left-Bottom
     * <li> Top-Right-Bottom
     * </ul>
     */
    static public class Path extends Timing {
        public List<Segment> segments = null;
        
        public Segment segment(String task) {
            Segment segment = new Segment(task);
            if (segments==null) {
                segments = new ArrayList<>();
            }
            segments.add(segment);
            return segment;
        }
    }
    
    /**
     * Timings for a task in a graph. Every path has at least one segment. 
     * Only root tasks will have only one segment.
     */
    static public class Segment extends Timing {
        public final String task;
        
        Segment(String task) {
            this.task = task;
        }
    }
    
    /**
     * Convenience method for tests.
     * @param name of graph
     * @return newly-created graph
     */
    Graph graph(String name) {
        Graph graph = new Graph(name);
        graphs.add(graph);
        return graph;
    }
}
