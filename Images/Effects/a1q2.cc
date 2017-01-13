#include <iostream>
#include <string>
#include <vector>
#include <fstream>
using namespace std;

int main() {

	//ifstream infile("thefile");
	int N;
	char command;

	cin >> N >> command;

	string ignore;

	getline(cin,ignore);

	if (N <= 0) {
		cerr << "Error, line length must be positive." << endl;
		return 0;
	}
	if (command!='f' && command !='r' && command != 'g')
	{
		cerr << "Error, command is illegal." << endl;
		return 0;
	}

	string line;
	vector<string>lines;
	while(!cin.fail())
	{
		getline(cin,line);
		if (cin.fail())
		{
			break;
		}

		//cout << line << endl;
		lines.push_back(line);
	}

	if (command =='f')
	{
		for (int i = 0; i < lines.size(); i++)
		{
			if (lines[i].size()>N)
			{
				lines[i]=lines[i].substr(0,N);
			}
			cout << lines[i] << endl;
		}

		return 0;
	}
	else if (command == 'r')
	{
		for (int i = lines.size()-1; i >=0; i--)
		{
			if (lines[i].size()>N)
			{
				lines[i]=lines[i].substr(0,N);
			}
			cout << lines[i] << endl;
		}

		return 0;
	}
	else if (command == 'g')
	{
		for (int i = 0; i < lines.size(); i++)
		{

			if (lines[i].find("fnord")==string::npos)
			{
				continue;
			}
			if (lines[i].size()>N)
			{
				lines[i]=lines[i].substr(0,N);
			}

			cout << lines[i] << endl;
		}

		return 0;
	}
}
