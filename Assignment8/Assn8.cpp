#include <iostream>

using namespace std;
int main(){
	int fp_count = 0,
	 int_count = 0;
	for (float i = 0; i < 1; i += 0.01) {
		fp_count++;
		cout << "fp_counter: " << i << endl;
	}
	for (int i = 0; i < 100; i += 1) {
		int_count++;
	}
	
	cout << "fp_count: " << fp_count << endl;
	cout << "int_count: " << int_count << endl;
	return 0;
}
