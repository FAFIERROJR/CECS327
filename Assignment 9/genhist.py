import javaobj
import matplotlib.pyplot as plot
import numpy

jobj = open("numhops.dat", "rb").read()
num_hops_arr= javaobj.loads(jobj)
num_hops_numpy = numpy.array(num_hops_arr)
weights = numpy.ones_like(num_hops_numpy) / (len(num_hops_numpy))
plot.hist(num_hops_numpy,weights=weights)

plot.show();