import { styled } from "styled-components";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

import DirectoryEditBox from "../components/DirectoryEditBox";

import { URL } from "../data/constants";

import authLoginCheck from "../utils/authLoginCheck";

const DirectoryEditPage = () => {
  const isLogin = authLoginCheck();
  if (!isLogin) {
    return window.location.replace("/login");
  }

  const navigate = useNavigate();
  const { accessToken } = JSON.parse(localStorage.getItem("localUser"));
  const [category, setCategory] = useState("");

  const changeCategory = (text) => {
    setCategory(text);
  };

  const postCategory = async () => {
    if (category.length === 0) {
      return alert("디렉토리명은 필수값입니다.");
    }
    if (category.length > 20) {
      return alert("디렉토리명은 20자 이내로 작성해주세요.");
    }

    await axios.post(
      `${URL}/todoStorages`,
      { category },
      {
        headers: { Authorization: `Bearer ${accessToken}` },
      },

      navigate("/directory"),
    );
  };

  return (
    <>
      <EditWrapper>
        <EditSection>
          <DirectoryEditBox
            EditPage={true}
            category={category}
            changeCategory={changeCategory}
            postCategory={postCategory}
          />
        </EditSection>
      </EditWrapper>
    </>
  );
};

export default DirectoryEditPage;

const EditWrapper = styled.div`
  height: 100%;

  display: flex;
  flex-direction: column;
  align-items: center;

  font-family: "HakgyoansimWoojuR";
`;

const EditSection = styled.section`
  width: 100%;
  max-width: 430px;
  height: 100%;

  background-color: #ffffff;

  padding-top: 80px;
  padding-bottom: 130px;

  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;

  overflow: auto;
`;
