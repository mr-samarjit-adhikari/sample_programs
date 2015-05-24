/* How to implement stack using template */

#include <iostream>

using namespace std;

template <class T>
class Stack{
	public:
		Stack<T>(unsigned int size);
		~Stack<T>();
		T pop();
		void push(T item);
	private:
		unsigned int top;
		unsigned int size;
		T*    buffer;
};

template <class T>
Stack<T>::Stack(unsigned int s):size(s),top(0){
	this->buffer = new T[s];
}

template <class T>
Stack<T>::~Stack(){delete buffer;}

template <class T>
void Stack<T>::push(T item){
	if(top<size){
		buffer[top++] = item;
		cout<<"Pushed: "<<item<<endl;
	}
	else{
		cout<<"Stack FULL"<<endl;
	}
}

template <class T>
T Stack<T>::pop(){
	T  ret ;
	if(0==top){
		cout<<"No Items in stack...\n"<<endl;
	}
	else{
		ret = buffer[--top];
		cout<<"Poped: "<<ret<<endl;
	}
	return ret;
}

main()
{
	Stack<int>  intStack(1);

	intStack.push(6);
	intStack.push(7);
	intStack.pop();
	intStack.pop();
}
