<div id="top"></div>

<!-- PROJECT SHIELDS -->
[![GitHub repo size][reposize-shield]](https://github.com/RamMichaeli17/Final-Project-Internet-Programming)
[![GitHub language count][languagescount-shield]](https://github.com/RamMichaeli17/Final-Project-Internet-Programming)
[![Contributors][contributors-shield]](https://github.com/RamMichaeli17/Final-Project-Internet-Programming/graphs/contributors)
[![Stargazers][stars-shield]](https://github.com/RamMichaeli17/Final-Project-Internet-Programming/stargazers)
[![LinkedIn][linkedin-shield]](https://linkedin.com/in/ram-michaeli)
[![Gmail][gmail-shield]](mailto:ram153486@gmail.com)

<!-- PROJECT LOGO -->
<br />
<div align="center">

<h3 align="center">Final Project - Internet Programming</h3>

  <p align="center">
    TCP server and client in multi-threading for algorithmic tasks on matrices.
    <br />
</div>

<!-- TABLE OF CONTENTS -->
<details>
  <summary>Table of Contents</summary>
  <ol>
    <li><a href="#about-the-project">About The Project</a></li>
    <li><a href="#build-requirements">Build Requirements</a></li>
    <li><a href="#usage">Usage</a></li>
    <li><a href="#contributors">Contributors</a></li>
    <li><a href="#contact">Contact</a></li>
  </ol>
</details>

<!-- ABOUT THE PROJECT -->
## About The Project

This project implements a TCP server and client that handle various algorithmic tasks related to matrices in a multi-threaded environment. The client can choose from four different main tasks:

1. **Find all SCCs in a graph (including diagonals)**:
   - **Input**: 2D array of integers
   - **Output**: All SCCs, sorted by size of the returned hashset.
   - **Example**:
     ```
       {1, 0, 0, 0, 1},
       {0, 0, 1, 0, 0},
       {0, 0, 1, 0, 0},
       {0, 0, 1, 0, 1},
       {0, 0, 1, 0, 1}
     ```
     **Output**: 
     ```
     [[(0,0)], [(0,4)], [(4,4), (3,4)], [(3,2), (2,2), (1,2), (4,2)]]
     ```

2. **Find all the shortest paths between two nodes**:
   - **Input**: 2D array of integers
   - **Output**: All the shortest paths between two nodes.
   - **Example**:
     ```
       {1, 0, 0, 0, 0},
       {0, 1, 1, 0, 0},
       {0, 1, 1, 0, 0},
       {0, 0, 1, 0, 0},
       {0, 0, 1, 0, 0}
     ```
     **Output** from (0,0) to (4,2):
     ```
     [[(0,0), (1,1), (2,1), (3,2), (4,2)], [(0,0), (1,1), (2,2), (3,2), (4,2)]]
     ```

3. **Find all submarines in matrix**:
   A "submarine" must satisfy the following rules:
   1. Minimum of two "1" vertically.
   2. Minimum of two "1" horizontally.
   3. No "1" diagonally unless rules 1 and 2 are satisfied.
   4. The minimal distance between two submarines must be at least one index ("0").

   - **Input**: 2D array of integers
   - **Output**: Number of proper submarines.
   - **Example**:
     ```
       {1, 1, 0, 0, 1},
       {0, 0, 0, 0, 1},
       {0, 0, 1, 0, 0},
       {1, 0, 1, 0, 0},
       {1, 0, 1, 0, 1}
     ```
     **Output**: 4

4. **Find all lightest weight paths between two nodes (also supports negative weights)**:
   - **Input**: 2D array of integers
   - **Output**: All the lightest weight paths between two nodes.
   - **Example**:
     ```
       {100, 100, 100},
       {300, 900, 500},
       {100, 100, 100}
     ```
     **Output** from (1,0) to (1,2):
     ```
     [[(1,0),(0,1),(1,2)],[(1,0),(2,1),(1,2)]], with weight 900.
     ```

<p align="right">(<a href="#top">back to top</a>)</p>

## Build Requirements

- Java JDK 8 or later
- IDE such as IntelliJ IDEA or Eclipse for development

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- USAGE EXAMPLES -->
## Usage

1. To use the project, select the task you want the client to perform.
2. Input the appropriate matrix data and execute the chosen task.

### Example Tasks

1. **Find all SCCs in a graph**.
2. **Find all shortest paths between two nodes**.
3. **Find all submarines in the matrix**.
4. **Find all lightest weight paths between two nodes**.

<p align="right">(<a href="#top">back to top</a>)</p>

## Contributors

We thank the following people who contributed to this project:

<table>
  <tr>
    <td align="center">
      <a href="https://github.com/RamMichaeli17">
        <img src="https://avatars.githubusercontent.com/u/62435713?v=4" width="100px;"/><br>
        <sub>
          <b>Ram Michaeli</b>
        </sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/SapirDaga1">
        <img src="https://avatars.githubusercontent.com/u/76609543?v=4" width="100px;"/><br>
        <sub>
          <b>Sapir Daga</b>
        </sub>
      </a>
    </td>
    <td align="center">
      <a href="https://github.com/ozweiss">
        <img src="https://avatars.githubusercontent.com/u/89026763?v=4" width="100px;"/><br>
        <sub>
          <b>Oz Weiss</b>
        </sub>
      </a>
    </td>
  </tr>
</table>

<p align="right">(<a href="#top">back to top</a>)</p>

<!-- CONTACT -->
## Contact

Ram Michaeli - ram153486@gmail.com

Project Link: [https://github.com/RamMichaeli17/Final-Project-Internet-Programming](https://github.com/RamMichaeli17/Final-Project-Internet-Programming)

<a href="mailto:ram153486@gmail.com"><img src="https://img.shields.io/twitter/url?label=Gmail%3A%20ram153486%40gmail.com&logo=gmail&style=social&url=https%3A%2F%2Fmailto%3Aram153486%40gmail.com"/></a>
<a href="https://linkedin.com/in/ram-michaeli"><img src="https://img.shields.io/twitter/url?label=Ram%20Michaeli&logo=linkedin&style=social&url=https%3A%2F%2Flinkedin.com%2Fin%2Fram-michaeli"/></a>
<p align="right">(<a href="#top">back to top</a>)</p>

<!-- MARKDOWN LINKS & IMAGES -->
[reposize-shield]: https://img.shields.io/github/repo-size/RamMichaeli17/Final-Project-Internet-Programming?style=for-the-badge
[languagescount-shield]: https://img.shields.io/github/languages/count/RamMichaeli17/Final-Project-Internet-Programming?style=for-the-badge
[contributors-shield]: https://img.shields.io/github/contributors/RamMichaeli17/Final-Project-Internet-Programming?style=for-the-badge
[stars-shield]: https://img.shields.io/github/stars/RamMichaeli17/Final-Project-Internet-Programming?style=for-the-badge
[linkedin-shield]: https://img.shields.io/badge/LinkedIn-Ram%20Michaeli-blue?style=for-the-badge&logo=linkedin
[gmail-shield]: https://img.shields.io/badge/Gmail-ram153486%40gmail.com-red?style=for-the-badge&logo=gmail
