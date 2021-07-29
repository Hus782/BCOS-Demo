/*
SPDX-License-Identifier: Apache-2.0
*/
//pragma solidity >0.5.11;
pragma solidity>=0.4.24 <0.6.11;

contract Vote {


    struct Voter {

            bool voted;  
            uint vote;
        }

    struct VoteItem {

            string name;
            uint ID;
            uint votenum;  

        } 


    mapping(address => Voter) public voters;

    VoteItem[] public items;  

    constructor() {

        items.push(VoteItem("First Choice", items.length, 0));
        items.push(VoteItem("Second Choice", items.length, 7));
        items.push(VoteItem("Third Choice", items.length, 12));

    }

    function get() public pure returns (string memory) {
        return "Hello, World!";
    }

    function getItemsCount() public view returns(uint256){
        return items.length;
    }


    function vote(uint ID) public  returns ( string memory){
        require(ID >=0  && ID < items.length, "Invalid item ID!");
        require(!voters[msg.sender].voted, "You have voted already!");
        voters[msg.sender].voted = true;
        items[ID].votenum += 1;

        return "Voted succesfully! ";

    }
 function getItem(uint _ID) public view returns (string memory, uint, uint) {
        require(_ID >=0  && _ID < items.length, "invalid item ID");
        return (items[_ID].name, items[_ID].ID, items[_ID].votenum);

    }
/*
    function getItem(uint _ID) public view returns (string memory, uint, uint) {
        require(_ID >=0  && _ID < items.length, "invalid item ID");
        return (items[_ID].name, items[_ID].ID, items[_ID].votenum);

    }


    function getAllItems() public view returns (string[] memory, uint[] memory,
        uint[] memory) {

        string[] memory names = new string[](items.length);
        uint[]    memory IDs = new uint[](items.length);
        uint[]    memory votes = new uint[](items.length);

        for (uint i = 0; i < items.length; i++) {
            names[i] = items[i].name;
            IDs[i] = items[i].ID;
            votes[i] = items[i].votenum;
        }

        return (names, IDs, votes);
    }
*/

}
